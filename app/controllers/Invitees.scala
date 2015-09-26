package controllers

import json.JSON
import play.api.Logger
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._
import play.api.mvc._
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.api.Cursor
import validations.Invitee

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.postfixOps

/**
 * Created by ka-son on 9/25/15.
 */
object Invitees extends Controller with MongoController with JSON with Invitee {

  /**
   * Invitee collection
   * Connect to the invitee collection
   * @return JSONCollection
   */
  private def inviteeCollection: JSONCollection = db.collection[JSONCollection]("invitee")

  /**
   * Email collection
   * Connect to the email collection
   * @return JSONCollection
   */
  private def emailCollection: JSONCollection = db.collection[JSONCollection]("email")

  /**
   * Find query
   * Access the database and find the target
   * Sort the invitee in ascending order
   * Return the invitee(s) if there is/are match(es)
   * Otherwise return not found messages
   * @param q: String
   * @return Future[JsValue]
   */
  private def queryFind(q: JsValue): Future[JsValue] = {
    // Perform the query and get a cursor of JsObject
    val cursor: Cursor[JsObject] = emailCollection
      .find(q)
      .cursor[JsObject]

    // Gather all the JsObjects in a Seq
    val futureUsersList: Future[Seq[JsObject]] = cursor.collect[Seq]()

    // If the Seq is empty, return not found
    // Otherwise, return the Seq in Json format
    futureUsersList.map { users =>
      if (users.isEmpty) {
        Json.obj("messages" -> Json.arr("Not found"))
      }
      else {
        Json.toJson(users)
      }
    }
  }

  /**
   * Print invitee
   * Extract the invitee's information
   * @param invitee JsValue
   * @return JsValue
   */
  private def inviteePrinting(invitee: JsValue): JsValue = {
    // Extract invitee's information
    val i = (invitee \ "invitee").as[String]
    val e = (invitee \ "email").as[String]

    val response = Json.obj("invitee" -> i, "email" -> e)

    response
  }

  /**
   * Extract invitee
   * Return Some(invitee) if a invitee is extracted
   * Otherwise, return None
   * @param inviteeJsValue JsValue
   * @return Option[JsValue]
   */
  private def extractInvitee(inviteeJsValue: JsValue): Option[JsValue] = {
    // Extract messages string if any
    val jv = (inviteeJsValue \ "messages").asOpt[JsValue]

    jv match {
      case Some(s) => None
      case None =>
        val users = inviteeJsValue.as[JsArray]
        val response = inviteePrinting(users(0))
        Some(response)
    }
  }

  /**
   * Extract invitees
   * Call inviteePrinting
   * Return Some(invitees) if invitees are extracted
   * Otherwise return None
   * @param inviteesJsValue JsValue
   * @return Option[JsValue]
   */
  private def extractInvitees(inviteesJsValue: JsValue): Option[JsValue] = {
    // Extract messages string if any
    val jv = (inviteesJsValue \ "messages").asOpt[JsValue]

    jv match {
      case Some(s) => None
      case None =>
        val invitees: JsArray = inviteesJsValue.as[JsArray]
        val inviteesSeq = invitees.value.map { invitee =>
          inviteePrinting(invitee)
        }
        val response = new JsArray(inviteesSeq)
        Some(response)
    }
  }

  /**
   * Find by email
   * Find the invitee by target
   * Call queryFind to find matched email in the database
   * Call extract invitee to get invitee from the query result
   * Return the invitee if a invitee found
   * Otherwise return None
   * @param target String
   * @return Future[Option[JsValue]]
   */
  private def findByEmail(target: String): Future[Option[JsValue]] = {
    // Execute queryFind function to access the database to find the login
    val q = Json.obj("email" -> target)
    val futureJsValue: Future[JsValue] = queryFind(q)

    futureJsValue.map { jsValue =>
      // Execute extractUser to extract the user from the query result
      val js: Option[JsValue] = extractInvitee(jsValue)

      js match {
        case r@Some(user) => r
        case None => None
      }
    }
  }

  /**
   * Find all invitees
   * List all the invitees who type is user only in the database
   * Return Ok if there are invitees found
   * Otherwise return NotFound
   * @return
   */
  def findAll: Action[AnyContent] = Action.async {
    // Execute queryFind function to access the database to find
    val q = Json.obj()
    val futureJsValue: Future[JsValue] = queryFind(q)

    futureJsValue.map {
      jsValue =>
        // Execute extractUser to extract the user from the query result
        val js = extractInvitees(jsValue)

        js match {
          case Some(invitees) =>
            Logger.info(invitees.toString)
            Ok(prettify(invitees)).as("application/json; charset=utf-8")
          case None =>
            Logger.info(jsValue.toString)
            NotFound(prettify(jsValue)).as("application/json; charset=utf-8")
        }
    }
  }

  /**
   * Parse json from post request
   * Return bad request if the json is invalid
   * Validate invitee inputs
   * If the invitee's email is already registered return BadRequest
   * Otherwise insert the new user to the database and return Created with the
   * new invitee
   * @return Action[JsValue]
   */
  def create: Action[JsValue] = Action.async(parse.json) { request =>
    // Create a transformer
    val transformer = Reads.jsPickBranch[JsString](__ \ "invitee") and
      Reads.jsPickBranch[JsString](__ \ "email") reduce

    // Transform the json format
    val transformedResult = request.body.transform(transformer).map {
      tr =>
        // Valid json format
        Some(tr)
    }.getOrElse {
      // Invalid json format
      None
    }

    transformedResult match {
      case Some(tr) =>
        // Valid json format

        val validatedInvitee = validateInvitee(tr)

        validatedInvitee match {
          case Some(messages: JsValue) =>
            // Invalid input
            val response: JsValue = Json.obj("messages" -> messages)
            Logger.info(response.toString)
            Future.successful(BadRequest(prettify(response)).as("application/json; charset=utf-8"))
          case None =>
            // Valid input

            // Retrieve invitee and email
            val invitee: String = (tr \ "invitee").as[String]
            val email: String = (tr \ "email").as[String]

            // Check if email existed in the database
            val emailQueryResult = findByEmail(email)

            emailQueryResult.map {
              // Email already existed return bad request
              case Some(e: JsValue) =>
                val response: JsValue =
                  Json.obj("messages" -> Json.arr("Email is already registered"))
                Logger.info(response.toString)
                BadRequest(prettify(response)).as("application/json; charset=utf-8")
              case None =>
                // Email does not existed in the database
                // Create a new user
                val newInvitee = Json.obj(
                  "invitee" -> invitee,
                  "email" -> email
                )

                emailCollection.insert(newInvitee).map {
                  r => Created
                }
                Logger.info(tr.toString)
                Created(prettify(tr)).as("application/json; charset=utf-8")
            }
        }
      case None =>
        // Invalid json format
        val response: JsValue = Json.obj("messages" -> Json.arr("Invalid Json"))
        Logger.info(response.toString)
        Future.successful(BadRequest(prettify(response)).as("application/json; charset=utf-8"))
    }
  }

}
