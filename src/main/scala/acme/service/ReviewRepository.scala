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
  def addReviews(reviews: Stream[IO, Review]): IO[Unit]
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
             avg(overall) 
        from review 
       where unixReviewTime between $int8 and $int8  
    group by asin
      having count(1) > $int4
    order by avg desc
       limit $int4;
       """
      .query(varchar(10) ~ float8)
      .map { case (asin, avg) => Result(Asin(asin), avg) }

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
