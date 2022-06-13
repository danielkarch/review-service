package acme
package service

import api.*
import cats.effect.*
import fs2.Stream
import java.time.LocalDate
import skunk.*
import skunk.implicits.*
import skunk.codec.all.*
import java.time.ZoneId
import java.time.ZoneOffset

trait ReviewRepository {

  /** Adds all reviews in the stream to the database.
    */
  def addReviews(reviews: Stream[IO, Review]): IO[Unit]

  /** Returns a list of the best-rated entries, considering reviews authored within the provided range. The output is
    * sorted by average score in descending order.
    * @param start
    *   the start of the range
    * @param end
    *   the end of the range
    * @param limit
    *   the maximum number of reviews to return
    * @param minNumberReviews
    *   only consider entries with at least this many reviews
    */
  def findReviews(start: LocalDate, end: LocalDate, limit: Int, minNumberReviews: Int): IO[List[Result]]
}

final class PostgresReviewRepository(session: Session[IO]) extends ReviewRepository {
  override def addReviews(reviews: Stream[IO, Review]): IO[Unit] =
    reviews.chunks
      .map(_.toList)
      .evalMap { revs =>
        session.prepare(insertMany(revs)).use(_.execute(revs)).void
      }
      .compile
      .drain

  override def findReviews(
      start: LocalDate,
      end: LocalDate,
      limit: Int,
      minNumberReviews: Int
  ): IO[List[Result]] = {
    val startInstant = start.atStartOfDay.atOffset(ZoneOffset.UTC).toEpochSecond
    val endInstant   = end.plusDays(1).atStartOfDay.atOffset(ZoneOffset.UTC).toEpochSecond

    val query = sql"""
      select asin, 
             cast(round(cast(avg(overall) as numeric), 2) as double precision) as avg
        from review 
       where unixReviewTime between $int8 and $int8  
    group by asin
      having count(1) > $int4
    order by avg desc
       limit $int4;
       """
      .query(varchar(10) ~ float8)
      .map(Result(_, _))

    session.prepare(query).use(ps => ps.stream(startInstant ~ endInstant ~ minNumberReviews ~ limit, 64).compile.toList)
  }

  private def insertMany(reviews: List[Review]) = {
    val enc = (varchar ~ varchar(10) ~ float8 ~ int8)
      .contramap((r: Review) => r.reviewerID ~ r.asin ~ r.overall.value ~ r.unixReviewTime)
      .values
      .list(reviews)
    sql"insert into review (reviewerID, asin, overall, unixReviewTime) values $enc on conflict (reviewerID, asin, overall, unixReviewTime) do nothing".command
  }

}
