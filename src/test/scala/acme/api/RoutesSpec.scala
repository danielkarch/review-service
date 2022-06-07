package acme.api

import weaver._
import acme.service.ReviewsServiceImpl
import org.http4s.HttpApp
import cats.effect.IO
import cats.effect.kernel.Resource
import org.http4s.Request
import org.http4s.Method
import org.http4s.implicits._

object RoutesSpec extends IOSuite {

  override type Res = HttpApp[IO]
  override def sharedResource =
    new Routes(ReviewsServiceImpl).all.map(_.orNotFound)

  def request(s: String) =
    Request[IO](
      method = Method.POST,
      uri = uri"/best-rated",
      body = fs2.Stream.emits(s.getBytes)
    )

  def post(service: HttpApp[IO], s: String) =
    service.run(request(s))

  def check(title: String)(body: String, expectedStatus: Int) =
    test(title) { (service, logger) =>
      post(service, body).flatMap { response =>
        response.body
          .through(fs2.text.utf8.decode)
          .evalMap(logger.error(_))
          .compile
          .drain
          .as(expect(response.status.code == expectedStatus))
      }
    }

  def example(
      start: String = "01.01.2010",
      end: String = "31.01.2010",
      limit: Int = 1,
      minNumberReviews: Int = 3
  ) =
    s"""{"start": "$start","end": "$end","limit": $limit,"min_number_reviews": $minNumberReviews}"""

  check("we accept a valid review")(
    body = example(),
    expectedStatus = 200
  )

  check("validate: start")(
    body = example(start = "00.00.2000"),
    expectedStatus = 400
  )

  check("validate: end")(
    body = example(end = "00.00.2000"),
    expectedStatus = 400
  )

  check("validate: limit")(
    body = example(limit = -3),
    expectedStatus = 400
  )

  check("validate: min_number_reviews")(
    body = example(minNumberReviews = -1),
    expectedStatus = 400
  )
}
