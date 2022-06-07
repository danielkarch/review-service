package acme

import smithy4s.http.json.JCodec.deriveJCodecFromSchema
import com.github.plokhotnyuk.jsoniter_scala.core._
import api.Review
import fs2.Stream
import fs2.io.file.Files
import fs2.io.file.Path
import fs2.text
import cats.effect.IO
import cats.syntax.all._

object Parser {

  def parseReview(line: String): Either[Throwable, Review] =
    Either.catchNonFatal(readFromString[Review](line))

  def parseFromFile(filename: String): Stream[IO, Review] =
    Files[IO]
      .readAll(Path(filename))
      .through(text.utf8.decode)
      .through(text.lines)
      .flatMap { line =>
        parseReview(line) match {
          case Left(e)  => Stream.exec(IO.println(e.getMessage))
          case Right(r) => Stream(r)
        }
      }

}