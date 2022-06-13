import Dependencies._

ThisBuild / organization      := "acme"
ThisBuild / scalaVersion      := "3.1.3"
Global / onChangedBuildSource := ReloadOnSourceChanges

lazy val root = (project in file("."))
  .enablePlugins(Smithy4sCodegenPlugin)
  .configs(IntegrationTest)
  .settings(
    name       := "review-service",
    run / fork := true,
    scalacOptions ++= Seq(
      "-no-indent",
      "-old-syntax"
    ),
    Defaults.itSettings,
    testFrameworks += new TestFramework("weaver.framework.CatsEffect"),
    libraryDependencies ++=
      Dependencies.smithy4s ++
        Dependencies.http4s ++
        Dependencies.weaver ++
        Dependencies.logging ++
        Dependencies.skunk ++
        Dependencies.postgres
  )
