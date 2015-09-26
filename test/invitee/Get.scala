package invitee

import json.JSON
import play.api.test._

import scala.concurrent._
import scala.concurrent.duration._

/**
 * Created by ka-son on 9/26/15.
 */
object Get extends PlaySpecification with JSON {

  val timeout: FiniteDuration = 10.seconds

  "GET /invitation must be 200 Ok" in {
    running(FakeApplication()) {
      val request = FakeRequest(GET, "/invitation")
      val response = route(request)
      response.isDefined mustEqual true
      val result = Await.result(response.get, timeout)

      result.header.status mustEqual 200
    }
  }

}
