package acme

import smithy4s.http.json.JCodec.deriveJCodecFromSchema
import com.github.plokhotnyuk.jsoniter_scala.core.*
import api.Review
import fs2.Stream
import fs2.io.file.Files
import fs2.io.file.Path
import fs2.text
import cats.effect.IO
import cats.syntax.all.*
import scala.io.Source
import org.typelevel.log4cats.Logger

object Parser {

  private def parseReview(line: String): Either[Throwable, Review] =
    Either.catchNonFatal(readFromString[Review](line))

  def parseReviews(reviews: Stream[IO, String])(using logger: Logger[IO]) =
    reviews.flatMap { line =>
      parseReview(line) match {
        case Left(e)  => Stream.exec(logger.warn(s"could not parse review: ${e.getMessage}"))
        case Right(r) => Stream(r)
      }
    }

  def parseFromFile(path: String)(using logger: Logger[IO]): Stream[IO, Review] =
    parseReviews(
      Files[IO]
        .readAll(Path(path))
        .through(text.utf8.decode)
        .through(text.lines)
        .filter(_.nonEmpty)
    )

}
