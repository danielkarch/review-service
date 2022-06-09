package acme

import weaver.*
import cats.effect.IO

object ParserSpec extends SimpleIOSuite {

  test("we can read reviews from a file") {
    Parser
      .parseFromFile(getClass.getResource("/input.txt").getPath)
      .compile
      .toList
      .map { reviews =>
        expect(reviews.nonEmpty)
      }
  }

}
