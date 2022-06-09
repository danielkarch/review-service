package acme.service

import weaver.*
import skunk.Session
import cats.effect.*
import natchez.Trace.Implicits.noop
import java.time.LocalDate

object ReviewRepositoryItSpec extends IOSuite {

  override type Res = Session[IO]
  override def sharedResource: Resource[IO, Res] = Session.single[IO](
    host = "localhost",
    port = 5432,
    user = "postgres",
    database = "reviewdb",
    password = Some("docker")
  )

  test("foo") { session =>
    val repo = new PostgresReviewRepository(session)
    repo.findReviews(LocalDate.now, LocalDate.now, 2, 2).map(results => expect(results.isEmpty))
  }
}
