package acme.service

import weaver.*
import skunk.Session
import cats.effect.*
import natchez.Trace.Implicits.noop
import java.time.LocalDate
import io.zonky.test.db.postgres.embedded.EmbeddedPostgres
import cats.syntax.all.*
import scala.io.Source
import acme.api.Review
import acme.api.Rating
import acme.api.{Result => DbResult}

object ReviewRepositoryItSpec extends IOSuite {

  override type Res = Session[IO]
  override def sharedResource: Resource[IO, Res] = startPostgres >> Session.single[IO](
    host = "localhost",
    port = 6543,
    user = "postgres",
    database = "postgres",
    password = None
  )

  def startPostgres: Resource[IO, EmbeddedPostgres] = {
    val release    = (pg: EmbeddedPostgres) => IO.delay(pg.close())
    val readSchema = IO.blocking(Source.fromResource("reviews.sql").getLines.mkString("\n"))
    val start      = IO.delay(EmbeddedPostgres.builder().setPort(6543).start())
    val acquire    = for {
      schema <- readSchema
      pg     <- start
      _      <- IO.delay {
                  val c = pg.getPostgresDatabase.getConnection
                  val s = c.createStatement()
                  s.executeUpdate(schema);
                }
    } yield pg

    Resource.make(acquire)(release)
  }

  test("test scenario") { session =>
    val repo = new PostgresReviewRepository(session)

    val reviews = List(
      Review(
        asin = "B000Q75VCO",
        helpful = List(16, 40),
        overall = Rating(2.0),
        reviewText = "...",
        reviewerID = "B07844AAA04E4",
        reviewerName = Some("Gaylord Bashirian"),
        summary = "...",
        unixReviewTime = 1475261866
      ),
      Review(
        asin = "B000NI7RW8",
        helpful = List(32, 52),
        overall = Rating(3.0),
        reviewText = "...",
        reviewerID = "4E82CF3A24D34",
        reviewerName = Some("Emilee Heidenreich"),
        summary = "...",
        unixReviewTime = 1455120950
      ),
      Review(
        asin = "B00000AQ4N",
        helpful = List(35, 57),
        overall = Rating(2.0),
        reviewText = "...",
        reviewerID = "7D04AF18AA084",
        reviewerName = Some("Shon Balistreri"),
        summary = "...",
        unixReviewTime = 1571581258
      ),
      Review(
        asin = "B000JQ0JNS",
        helpful = List(32, 87),
        overall = Rating(4.0),
        reviewText = "...",
        reviewerID = "53110BA721544",
        reviewerName = Some("Lisa Batz"),
        summary = "...",
        unixReviewTime = 1466668179
      ),
      Review(
        asin = "B000KFZ32A",
        helpful = List(5, 19),
        overall = Rating(3.0),
        reviewText = "...",
        reviewerID = "539457305BE84",
        reviewerName = Some("Voncile Heathcote"),
        summary = "...",
        unixReviewTime = 1404997356
      ),
      Review(
        asin = "B00000AQ4N",
        helpful = List(67, 69),
        overall = Rating(4.0),
        reviewText = "...",
        reviewerID = "C7812FD6D0464",
        reviewerName = Some("Cinderella Wunsch"),
        summary = "...",
        unixReviewTime = 1270258819
      ),
      Review(
        asin = "B000NI7RW8",
        helpful = List(0, 24),
        overall = Rating(4.0),
        reviewText = "...",
        reviewerID = "761045EEC00D4",
        reviewerName = Some("Luisa Kling"),
        summary = "...",
        unixReviewTime = 1447118407
      ),
      Review(
        asin = "B000KFZ32A",
        helpful = List(23, 23),
        overall = Rating(1.0),
        reviewText = "...",
        reviewerID = "E7A5F7E40C8D4",
        reviewerName = Some("Micah Robel"),
        summary = "...",
        unixReviewTime = 1347189467
      ),
      Review(
        asin = "B0002F40AY",
        helpful = List(0, 1),
        overall = Rating(5.0),
        reviewText = "...",
        reviewerID = "FC7F1F6A10354",
        reviewerName = Some("Lynelle Robel"),
        summary = "...",
        unixReviewTime = 1348778489
      ),
      Review(
        asin = "B000NI7RW8",
        helpful = List(0, 86),
        overall = Rating(4.0),
        reviewText = "...",
        reviewerID = "1533FADBABEA4",
        reviewerName = Some("Wilburn Mohr"),
        summary = "...",
        unixReviewTime = 1339051628
      ),
      Review(
        asin = "B000654P8C",
        helpful = List(74, 75),
        overall = Rating(3.0),
        reviewText = "...",
        reviewerID = "392704CA61D64",
        reviewerName = Some("Homer Walter"),
        summary = "...",
        unixReviewTime = 1305588946
      ),
      Review(
        asin = "B0002F40AY",
        helpful = List(17, 27),
        overall = Rating(2.0),
        reviewText = "...",
        reviewerID = "A23670C1E18E4",
        reviewerName = Some("Donte Deckow"),
        summary = "...",
        unixReviewTime = 1342596834
      ),
      Review(
        asin = "B000654P8C",
        helpful = List(3, 76),
        overall = Rating(2.0),
        reviewText = "...",
        reviewerID = "A35CECDD3AEB4",
        reviewerName = Some("Douglass Jacobs"),
        summary = "...",
        unixReviewTime = 1522847344
      ),
      Review(
        asin = "B000JQ0JNS",
        helpful = List(23, 54),
        overall = Rating(5.0),
        reviewText = "...",
        reviewerID = "7A2294BB37D54",
        reviewerName = Some("Adeline Langosh"),
        summary = "...",
        unixReviewTime = 1476369800
      ),
      Review(
        asin = "B0002F40AY",
        helpful = List(6, 12),
        overall = Rating(3.0),
        reviewText = "...",
        reviewerID = "CAFC0D7AE9464",
        reviewerName = Some("Domenic Cremin"),
        summary = "...",
        unixReviewTime = 1543546718
      )
    )

    repo.addReviews(fs2.Stream.emits(reviews)) >>
      repo
        .findReviews(LocalDate.of(2010, 1, 1), LocalDate.of(2020, 12, 31), 2, 0)
        .map(results =>
          expect(
            results == List(
              DbResult(
                asin = "B000JQ0JNS",
                averageRating = 4.5
              ),
              DbResult(
                asin = "B000NI7RW8",
                averageRating = 3.67
              )
            )
          )
        )
  }
}
