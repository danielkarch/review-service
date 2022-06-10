package acme

import weaver.*
import cats.effect.IO
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import fs2.Stream
import cats.effect.kernel.Resource

object ParserSpec extends IOSuite {

  override type Res = Logger[IO]
  override def sharedResource: Resource[IO, Res] = Resource.eval(Slf4jLogger.create[IO])

  test("we can read reviews from a file") { logger =>
    given Logger[IO] = logger
    Parser
      .parseFromFile(getClass.getResource("/input.txt").getPath)
      .compile
      .toList
      .map { reviews =>
        expect(reviews.nonEmpty)
      }
  }

  test("we ignore malformed input") { logger =>
    given Logger[IO] = logger

    val lines = List(
      """{"asin":"B000654P8C","helpful":[3,76],"overall":2.0,"reviewText":"It is our choices, Harry, that show what we truly are, far more than our abilities.","reviewerID":"A35CECDD3AEB4","reviewerName":"Douglass Jacobs","summary":"Possimus quae labore.","unixReviewTime":1522847344}""",
      """ronk.""",
      """{"asin":"B0002F40AY","helpful":[6,12],"overall":3.0,"reviewText":"We could all have been killed - or worse, expelled.","reviewerID":"CAFC0D7AE9464","reviewerName":"Domenic Cremin","summary":"Qui asperiores ut maxime qui nihil neque.","unixReviewTime":1543546718}"""
    )

    Parser
      .parseReviews(Stream.emits(lines))
      .compile
      .toList
      .map(reviews => expect(reviews.map(_.asin) == List("B000654P8C", "B0002F40AY")))
  }

}
