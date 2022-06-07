package acme
package service

import api._
import cats.effect._
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.util.Try
import cats.syntax.all._
import cats.data.Validated._

object ReviewsServiceImpl extends ReviewsService[IO] {
  def bestRated(
      start: Date,
      end: Date,
      limit: Int,
      min_number_reviews: Int
  ): IO[BestRatedOutput] = (
    parseDate(start).toValidNec("invalid start date"),
    parseDate(end).toValidNec("invalid end date"),
    (limit >= 0).guard[Option].toValidNec("invalid limit"),
    (min_number_reviews >= 0).guard[Option].toValidNec("invalid min_number_reviews")
  ).tupled match {
    case Invalid(errors) => IO.raiseError(BadInput(errors.mkString_(";")))
    case Valid(_)        => IO.pure(BestRatedOutput(Nil))
  }

  private val datePattern = DateTimeFormatter.ofPattern("dd.MM.yyyy")

  private def parseDate(date: Date): Option[LocalDate] =
    Try(LocalDate.parse(date.value, datePattern)).toOption
}
