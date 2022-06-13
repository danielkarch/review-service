package acme.api

import cats.effect.*
import org.http4s.HttpRoutes
import smithy4s.http4s.SimpleRestJsonBuilder
import cats.syntax.all.*

final class Routes(reviewService: ReviewService[IO]) {

  private val reviews: Resource[IO, HttpRoutes[IO]] =
    SimpleRestJsonBuilder.routes(reviewService).resource

  private val docs: HttpRoutes[IO] =
    smithy4s.http4s.swagger.docs[IO](ReviewService)

  val all: Resource[IO, HttpRoutes[IO]] = reviews.map(_ <+> docs)
}
