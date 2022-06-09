import Dependencies._

ThisBuild / organization      := "acme"
ThisBuild / scalaVersion      := "3.1.2"
Global / onChangedBuildSource := ReloadOnSourceChanges

lazy val root = (project in file("."))
  .enablePlugins(Smithy4sCodegenPlugin)
  .configs(IntegrationTest)
  .settings(
    name := "reviews-service",
    Defaults.itSettings,
    testFrameworks += new TestFramework("weaver.framework.CatsEffect"),
    libraryDependencies ++=
      Dependencies.catsEffect ++
        Dependencies.smithy4s ++
        Dependencies.http4s ++
        Dependencies.weaver ++
        Dependencies.logging ++
        Dependencies.skunk
  )
