import sbt._

object Dependencies {

  lazy val compileDeps = Seq(sprayJson, cats)

  lazy val testDeps = Seq(scalaTest)

  // compile
  val sprayJson: ModuleID = "io.spray" %% "spray-json" % "1.3.4"
  val cats: ModuleID = "org.typelevel" %% "cats-core" % "1.0.1"

  // test
  val scalaTest: ModuleID = "org.scalatest" %% "scalatest" % "3.0.5" % "test"
}
