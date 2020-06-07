import xerial.sbt.Sonatype._

ThisBuild / scalaVersion := "2.13.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "non-nullable-fields",
    organization := "io.github.michaelt293",
    licenses := Seq(
      "APL2" -> url("https://www.apache.org/licenses/LICENSE-2.0.txt")
    ),
    description := "Assert non-nullable class fields",
    version := "0.1",
    sonatypeProjectHosting := Some(
      GitHubHosting(
        "Michaelt293",
        "non-nullable-fields",
        "Michaelt293@gmail.com"
      )
    ),
    // publish to the sonatype repository
    publishTo := sonatypePublishTo.value,
    libraryDependencies += "org.scala-lang" % "scala-reflect" % "2.13.2",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.1.1" % Test,
    scalacOptions ++= Seq(
      "-Ymacro-annotations"
    )
  )
