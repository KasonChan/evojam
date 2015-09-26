package invitee

import json.JSON
import play.api.libs.json.Json
import play.api.test._

import scala.concurrent._
import scala.concurrent.duration._

/**
 * Created by ka-son on 9/26/15.
 */
object Post extends PlaySpecification with JSON {

  val timeout: FiniteDuration = 10.seconds

  "POST /invitation " +
    """{
      |  "invitee": "John Smith",
      |  "email": "john@smith.mx"
      |} """ +
    "must be 201 Created" in {
    running(FakeApplication()) {
      val request = FakeRequest(POST, "/invitation")
        .withJsonBody(Json.parse( """{
                                    |  "invitee": "John Smith",
                                    |  "email": "john@smith.mx"
                                    |}""".stripMargin))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)

      val json = Json.parse(contentAsString(response.get))
      (json \ "invitee").as[String] mustEqual "John Smith"
      (json \ "email").as[String] mustEqual "john@smith.mx"
      result.header.status mustEqual 201
    }
  }

  "POST /invitation " +
    """{
      |  "invitee": "a",
      |  "email": "a@a.com"
      |} """ +
    "must be 201 Created" in {
    running(FakeApplication()) {
      val request = FakeRequest(POST, "/invitation")
        .withJsonBody(Json.parse( """{
                                    |  "invitee": "a",
                                    |  "email": "a@a.com"
                                    |}""".stripMargin))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)

      val expectedResponse = Json.obj("messages" -> Json.arr("Email is already registered"))
      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 400
    }
  }

  "POST /invitation " +
    """{
      |  "invitee": "",
      |  "email": "a@a.com"
      |} """ +
    "must be 400 Bad request" in {
    running(FakeApplication()) {
      val request = FakeRequest(POST, "/invitation")
        .withJsonBody(Json.parse( """{
                                    |  "invitee": "",
                                    |  "email": "a@a.com"
                                    |}""".stripMargin))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)

      val expectedResponse = Json.obj("messages" -> Json.arr("Invitee must be at least 1 character and at most 50 characters"))

      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 400
    }
  }

  "POST /invitation " +
    """{
      |  "invitee": "a",
      |  "email": ""
      |} """ +
    "must be 400 Bad request" in {
    running(FakeApplication()) {
      val request = FakeRequest(POST, "/invitation")
        .withJsonBody(Json.parse( """{
                                    |  "invitee": "a",
                                    |  "email": ""
                                    |}""".stripMargin))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)

      val expectedResponse = Json.obj("messages" -> Json.arr("Doesn't look like a valid email"))

      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 400
    }
  }

  "POST /invitation " +
    """{
      |  "invitee": "",
      |  "email": ""
      |} """ +
    "must be 400 Bad request" in {
    running(FakeApplication()) {
      val request = FakeRequest(POST, "/invitation")
        .withJsonBody(Json.parse( """{
                                    |  "invitee": "",
                                    |  "email": ""
                                    |}""".stripMargin))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)

      val expectedResponse = Json.obj("messages" -> Json.arr("Invitee must be at least 1 character and at most 50 characters",
        "Doesn't look like a valid email"))

      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 400
    }
  }

  "POST /invitation " +
    """{
      |  "email": ""
      |} """ +
    "must be 400 Bad request" in {
    running(FakeApplication()) {
      val request = FakeRequest(POST, "/invitation")
        .withJsonBody(Json.parse( """{
                                    |  "email": ""
                                    |}""".stripMargin))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)

      val expectedResponse = Json.obj("messages" -> Json.arr("Invalid Json"))

      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 400
    }
  }

  "POST /invitation " +
    """{ } """ +
    "must be 400 Bad request" in {
    running(FakeApplication()) {
      val request = FakeRequest(POST, "/invitation")
        .withJsonBody(Json.parse( """{ }""".stripMargin))
      val response = route(request)
      Thread.sleep(5000)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)

      val expectedResponse = Json.obj("messages" -> Json.arr("Invalid Json"))

      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 400
    }
  }

}
