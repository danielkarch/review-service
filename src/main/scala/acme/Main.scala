package acme

import api.*
import service.*
import cats.effect.*
import cats.implicits.*
import org.http4s.implicits.*
import org.http4s.ember.server.*
import org.http4s.*
import com.comcast.ip4s.*
import smithy4s.http4s.SimpleRestJsonBuilder
import com.github.plokhotnyuk.jsoniter_scala.core.JsonValueCodec
import skunk.Session
import skunk.implicits.*
import skunk.codec.all.*
import org.http4s.server.middleware
import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.typelevel.log4cats.Logger
import acme.persistence.Database

object Main extends IOApp {

  /** Entrypoint to the application. Provide the path to the input file as the first argument.
    *
    * E.g.: {{{./sbt "run data/input.txt"}}}
    */
  def run(args: List[String]): IO[ExitCode] =
    args.headOption match {
      case None       => IO.println("Error: Please provide a path to an input file.").as(ExitCode.Error)
      case Some(path) => runWithFile(args.head)
    }

  def runWithFile(path: String): IO[ExitCode] =
    Database.makeSession.use { session =>
      for {
        given Logger[IO] <- Slf4jLogger.create[IO]
        _                <- Logger[IO].info("starting up")
        reviewRepository  = PostgresReviewRepository(session)
        reviews           = Parser.parseFromFile(path)
        _                <- reviewRepository.addReviews(reviews)
        reviewService     = ValidatedReviewService(reviewRepository)
        buildRoutes       = Routes(reviewService).all
        server            = buildRoutes.flatMap(Server.apply)
        _                <- server.use(_ => IO.never)
      } yield ExitCode.Success
    }

}
