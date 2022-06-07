import cats.data.Validated._
import cats.syntax.all._
import acme.Parser.*

val review = """{"asin":"B000NI7RW8","helpful":[0,86],"overall":4.0,"reviewText":"It takes a great deal of bravery to stand up to our enemies, but just as much to stand up to our friends.","reviewerID":"1533FADBABEA4","reviewerName":"Wilburn Mohr","summary":"Sapiente aspernatur ut.","unixReviewTime":1339051628}"""
parseReview(review)

import cats.syntax.all._

val v = (Option.empty[Int].toValidNec("foo"), Option.empty[Int].toValidNec("baz"), Some(1).toValidNec("bar")).tupled 

v match {
    case Invalid(c) => c.toString
    case Valid(i) => i.toString
}
