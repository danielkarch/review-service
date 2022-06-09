package acme

import api._
import service._
import cats.effect._
import cats.implicits._
import org.http4s.implicits._
import org.http4s.ember.server._
import org.http4s._
import com.comcast.ip4s._
import smithy4s.http4s.SimpleRestJsonBuilder
import com.github.plokhotnyuk.jsoniter_scala.core.JsonValueCodec
import skunk.Session
import skunk.implicits.*
import skunk.codec.all.*
import org.http4s.server.middleware
import org.typelevel.log4cats.slf4j.Slf4jLogger
import acme.persistence.Database

object Main extends IOApp {

  def run(args: List[String]): IO[ExitCode] =
    args.headOption match {
      case None       => IO.println("Error: Please provide a path to an input file.").as(ExitCode.Error)
      case Some(path) => runWithFile(args.head)
    }

  def runWithFile(path: String): IO[ExitCode] =
    Database.makeSession.use { session =>
      for {
        logger          <- Slf4jLogger.create[IO]
        _               <- logger.info("starting up")
        reviewRepository = PostgresReviewRepository(session)
        reviews          = Parser.parseFromFile(path)
        _               <- reviewRepository.addReviews(reviews)
        reviewService    = ValidatedReviewService(reviewRepository)
        buildRoutes      = Routes(reviewService).all
        server           = buildRoutes.flatMap(Server.apply)
        _               <- server.use(_ => IO.never)
      } yield ExitCode.Success
    }

}
