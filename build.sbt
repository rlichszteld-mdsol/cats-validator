import sbt.Keys.scalaVersion

lazy val commonSettings = Seq(
  version := "1.0",
  scalaVersion := "2.12.6"
)

lazy val root = (project in file("."))
  .aggregate(core, example)

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
