package acme
package service

import api.*
import cats.effect.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.util.Try
import cats.syntax.all.*
import cats.data.Validated.*

trait ValidatedReviewService extends ReviewService[IO] {

  private def validate(
      start: Date,
      end: Date,
      limit: Int,
      minNumberReviews: Int
  ): IO[(LocalDate, LocalDate, Int, Int)] = (
    parseDate(start).toValidNec("invalid start date"),
    parseDate(end).toValidNec("invalid end date"),
    (limit >= 0).guard[Option].toValidNec("invalid limit"),
    (minNumberReviews >= 0).guard[Option].toValidNec("invalid min_number_reviews")
  ).tupled match {
    case Invalid(errors)         => IO.raiseError(BadInput(errors.mkString_(";")))
    case Valid(start, end, _, _) => IO.pure(start, end, limit, minNumberReviews)
  }

  override final def bestRated(
      start: Date,
      end: Date,
      limit: Int,
      minNumberReviews: Int
  ): IO[BestRatedOutput] = validate(start, end, limit, minNumberReviews).flatMap(bestRated(_, _, _, _))

  def bestRated(
      start: LocalDate,
      end: LocalDate,
      limit: Int,
      minNumberReviews: Int
  ): IO[BestRatedOutput]

  private val datePattern = DateTimeFormatter.ofPattern("dd.MM.yyyy")

  private def parseDate(date: Date): Option[LocalDate] =
    Try(LocalDate.parse(date.value, datePattern)).toOption
}

object ValidatedReviewService {
  def apply(reviewRepository: ReviewRepository) = new ValidatedReviewService {
    override def bestRated(
        start: LocalDate,
        end: LocalDate,
        limit: Int,
        min_number_reviews: Int
    ): IO[BestRatedOutput] =
      reviewRepository.findReviews(start, end, limit, min_number_reviews).map(BestRatedOutput(_))

  }
}
