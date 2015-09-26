package application

import json.JSON
import play.api.libs.json.{JsValue, Json}
import play.api.test._

import scala.concurrent._
import scala.concurrent.duration._

/**
 * Created by ka-son on 9/25/15.
 */
object Get extends PlaySpecification with JSON {

  val timeout: FiniteDuration = 10.seconds

  "GET /none must be None" in {
    running(FakeApplication()) {
      val request = FakeRequest(GET, "/none")
      val response = route(request)
      response must beNone
    }
  }

  "GET /test must be None" in {
    running(FakeApplication()) {
      val request = FakeRequest(GET, "/test")
      val response = route(request)
      response must beNone
    }
  }

  "GET / must be 200 Ok" in {
    running(FakeApplication()) {
      val request = FakeRequest(GET, "/")
      val response = route(request)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)
      val expectedResponse: JsValue =
        Json.obj("invitation_url" -> "/invitation",
          "documentation_url" -> "https://github.com/KasonChan/evojam/blob/master/README.md")

      contentAsString(response.get) mustEqual prettify(expectedResponse)
      result.header.status mustEqual 200
    }
  }

}
