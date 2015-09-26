package controllers

import json.JSON
import play.api.libs.json.JsValue
import play.api.mvc._
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection

import scala.language.postfixOps

/**
 * Created by ka-son on 9/25/15.
 */
object Invitees extends Controller with MongoController with JSON {

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

  def create: Action[JsValue] = ???

}
