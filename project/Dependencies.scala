import sbt.*

object Dependencies {
  object Versions {
    val smithy4s   = "0.14.2"
    val http4s     = "0.23.12"
    val weaver     = "0.7.12"
    val woof       = "0.4.4"
    val skunk      = "0.2.3"
    val logback    = "1.3.0-alpha16"
    val log4cats   = "2.3.1"
    val pureconfig = "0.17.1"
  }
  
  lazy val smithy4s = Seq(
    "com.disneystreaming.smithy4s" %% "smithy4s-http4s",
    "com.disneystreaming.smithy4s" %% "smithy4s-http4s-swagger"
  ).map(_ % Versions.smithy4s)

  lazy val http4s = Seq(
    "org.http4s" %% "http4s-ember-server" % Versions.http4s
  )

  lazy val weaver = Seq(
    "com.disneystreaming" %% "weaver-cats" % Versions.weaver % "it,test"
  )

  lazy val woof = Seq(
    "org.legogroup" %% "woof-core" % Versions.woof
    // "org.apache.logging.log4j" % "log4j-slf4j-impl" % "2.17.2",
    // "org.legogroup"           %% "woof-slf4j"       % Versions.woof // This does not work for some reason.
  )

  lazy val logging = Seq(
    "org.typelevel" %% "log4cats-slf4j"  % Versions.log4cats,
    "ch.qos.logback" % "logback-classic" % Versions.logback
  )

  lazy val skunk = Seq(
    "org.tpolecat" %% "skunk-core"
  ).map(_ % Versions.skunk)

  lazy val pureconfig = Seq(
    "com.github.pureconfig" %% "pureconfig",
    "com.github.pureconfig" %% "pureconfig-cats-effect"
  ).map(_ % Versions.pureconfig)

  lazy val postgres = Seq(
    "io.zonky.test" % "embedded-postgres" % "1.3.1" % "it"    
  )
}
