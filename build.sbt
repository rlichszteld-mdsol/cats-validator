
name := "cats-validator"

version := "1.0"

scalaVersion := "2.12.6"

libraryDependencies ++= Seq(
  "io.spray" %% "spray-json" % "1.3.4",
  "org.scalatest" %% "scalatest" % "3.0.5" % "test",
  "org.typelevel" %% "cats-core" % "1.0.1"
)
