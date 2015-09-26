package validations

import play.api.libs.json.{JsValue, Json}

/**
 * Created by ka-son on 9/26/15.
 */
trait Invitee {

  /**
   * Check invitee
   * Valid length range: 1 - 50
   * Return None if invitee is valid
   * Otherwise return Some error message
   * @param invitee Option[String]
   * @return Option[String]
   */
  def checkInvitee(invitee: Option[String]): Option[String] = {
    invitee match {
      case Some(i: String) =>
        if ((i.length < 1) || (i.length > 50))
          Some("Invitee must be at least 1 character and at most 50 characters")
        else
          None
      case None => Some("Invitee is required")
      case _ => Some("Invalid invitee")
    }
  }

  /**
   * Check email
   * Valid format: ([a-zA-Z0-9]+)@([a-zA-Z0-9]+)(\.)([a-zA-Z0-9]+)
   * Return None if the email is valid
   * Otherwise return Some error message
   * @param email Option[String]
   * @return Option[String]
   */
  def checkEmail(email: Option[String]): Option[String] = {
    email match {
      case Some(e: String) =>
        val emailPattern = """([a-zA-Z0-9._]+)@([a-zA-Z0-9._]+)(\.)([a-zA-Z0-9]+)"""

        if (!e.matches(emailPattern))
          Some("Doesn't look like a valid email")
        else
          None
      case None => Some("Email is required")
      case _ => Some("Invalid email")
    }
  }

  /**
   * Validate invitee
   * Call checkInvitee, checkEmail functions to check the invitee's info
   * Returns None if the user is valid
   * Otherwise return Some error message
   * @param jsValue JsValue
   * @return Option[JsValue]
   */
  def validateInvitee(jsValue: JsValue): Option[JsValue] = {
    val login: Option[String] = (jsValue \ "invitee").asOpt[String]
    val email: Option[String] = (jsValue \ "email").asOpt[String]

    val lv: Option[String] = checkInvitee(login)
    val ev: Option[String] = checkEmail(email)

    val resultSeq: Seq[Option[String]] = Seq(lv, ev)

    val resultSome: Seq[Option[String]] = resultSeq.filter(r => r.isDefined)

    resultSome match {
      case Seq() => None
      case rs: Seq[Option[String]] =>
        val result: Seq[String] = resultSome.map(x => x.getOrElse(""))
        val resultJs: JsValue = Json.toJson(result)
        Some(resultJs)
    }
  }

}
