package controllers

import json.JSON
import play.api.Logger
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._
import play.api.mvc._
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection
import validations.Invitee

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
  def inviteeCollection: JSONCollection = db.collection[JSONCollection]("invitee")

  /**
   * Email collection
   * Connect to the email collection
   * @return JSONCollection
   */
  def emailCollection: JSONCollection = db.collection[JSONCollection]("email")

  def findAll: Action[JsValue] = ???

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
            Logger.info(tr.toString)
            Future.successful(Created(prettify(tr)).as("application/json; charset=utf-8"))
        }
      case None =>
        // Invalid json format
        val response: JsValue = Json.obj("messages" -> Json.arr("Invalid Json"))
        Logger.info(response.toString)
        Future.successful(BadRequest(prettify(response)).as("application/json; charset=utf-8"))
    }
  }

}
