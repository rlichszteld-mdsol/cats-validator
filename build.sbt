import sbt.Keys.scalaVersion

lazy val commonSettings = Seq(
  version := "1.0",
  scalaVersion := "2.12.6"
)

lazy val root = (project in file("."))
  .aggregate(core, example, jsonParsing)

lazy val core = (project in file("core"))
  .settings(
    commonSettings,
    name := "cats-validation-core",
    libraryDependencies ++= Dependencies.compileDeps ++ Dependencies.testDeps
  )

lazy val example = (project in file("example"))
  .settings(
    commonSettings,
    name := "cats-validation-example",
    libraryDependencies ++= Dependencies.compileDeps ++ Dependencies.testDeps
  )
  .dependsOn(core % "compile->compile;test->test")

lazy val jsonParsing = (project in file("json-parsing"))
  .settings(
    commonSettings,
    name := "json-parsing",
    libraryDependencies ++= Dependencies.compileDeps ++ Dependencies.testDeps
  )
