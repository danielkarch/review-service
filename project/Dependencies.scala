import sbt._

object Dependencies {
  object Versions {
    val catsEffect = "3.3.12"
    val smithy4s = "0.13.5"
    val http4s = "0.23.12"
    val weaver = "0.7.12"
    val woof = "0.4.4"
  }

  lazy val catsEffect = Seq(
    "org.typelevel" %% "cats-effect",
    "org.typelevel" %% "cats-effect-kernel",
    "org.typelevel" %% "cats-effect-std"
  ).map(_ % Versions.catsEffect)

  lazy val smithy4s = Seq(
    "com.disneystreaming.smithy4s" %% "smithy4s-http4s",
    "com.disneystreaming.smithy4s" %% "smithy4s-http4s-swagger"
  ).map(_ % Versions.smithy4s)

  lazy val http4s = Seq(
    "org.http4s" %% "http4s-ember-server" % Versions.http4s
  )

  lazy val weaver = Seq(
    "com.disneystreaming" %% "weaver-cats" % Versions.weaver % Test
  )

  lazy val woof = Seq(
    "org.legogroup" %% "woof-core",
    "org.legogroup" %% "woof-slf4j"
  ).map(_ % Versions.woof)

}
