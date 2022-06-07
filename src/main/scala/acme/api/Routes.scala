package acme.api

import cats.effect._
import org.http4s.HttpRoutes
import smithy4s.http4s.SimpleRestJsonBuilder
import cats.syntax.all._

final class Routes(reviewsService: ReviewsService[IO]) {
  
  private val example: Resource[IO, HttpRoutes[IO]] =
    SimpleRestJsonBuilder.routes(reviewsService).resource

  private val docs: HttpRoutes[IO] =
    smithy4s.http4s.swagger.docs[IO](ReviewsService)

  val all: Resource[IO, HttpRoutes[IO]] = example.map(_ <+> docs)
}
