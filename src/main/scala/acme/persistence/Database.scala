package acme.persistence

import skunk.Session
import cats.effect.IO
import natchez.Trace.Implicits.noop

object Database {
  val makeSession = Session.single[IO](
    host = "localhost",
    port = 5432,
    user = "postgres",
    database = "reviewdb",
    password = Some("docker")
  )
}
