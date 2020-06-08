import xerial.sbt.Sonatype._

lazy val scala213 = "2.13.1"
lazy val scala212 = "2.12.11"
lazy val scala211 = "2.11.12"
lazy val supportedScalaVersions = List(scala213, scala212, scala211)
lazy val macroParadise = compilerPlugin(
  "org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full
)

ThisBuild / scalaVersion := scala213

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
    crossScalaVersions := supportedScalaVersions,
    sonatypeProjectHosting := Some(
      GitHubHosting(
        "Michaelt293",
        "non-nullable-fields",
        "Michaelt293@gmail.com"
      )
    ),
    // publish to the sonatype repository
    publishTo := sonatypePublishTo.value,
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.1.1" % Test,
    libraryDependencies ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, n)) if n >= 13 =>
          List("org.scala-lang" % "scala-reflect" % scala213)
        case Some((2, 12)) =>
          List("org.scala-lang" % "scala-reflect" % scala212, macroParadise)
        case _ =>
          List("org.scala-lang" % "scala-reflect" % scala211, macroParadise)
      }
    },
    scalacOptions ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, n)) if n >= 13 => "-Ymacro-annotations" :: Nil
        case _                       => Nil
      }
    }
  )
