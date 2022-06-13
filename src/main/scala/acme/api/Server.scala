package acme.api

import org.http4s.HttpRoutes
import cats.effect.*
import org.http4s.ember.server.EmberServerBuilder
import com.comcast.ip4s.*
import org.http4s.server.middleware
import org.http4s.server.Server

object Server {
  def apply(routes: HttpRoutes[IO]): Resource[IO, Server] =
    EmberServerBuilder
      .default[IO]
      .withPort(port"8086")
      .withHost(host"localhost")
      .withHttpApp(routes.orNotFound)
      .build
}
