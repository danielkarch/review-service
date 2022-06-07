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
import org.legogroup.woof.Logger
import org.legogroup.woof.{given, *}

object Main extends IOApp.Simple {

  given Filter = Filter.everything
  given Printer = ColorPrinter()

  val server = new Routes(ReviewsServiceImpl).all
    .flatMap { routes =>
      EmberServerBuilder
        .default[IO]
        .withPort(port"8080")
        .withHost(host"localhost")
        .withHttpApp(routes.orNotFound)
        .build
    }

  val run = for {
    logger <- DefaultLogger.makeIo(Output.fromConsole)
    _ <- logger.info("starting up")
    _ <- server.use(_ => IO.never)
  } yield ()

}
