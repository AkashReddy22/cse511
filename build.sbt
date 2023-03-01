import sbt.Keys.{libraryDependencies, scalaVersion, version}

lazy val root = (project in file(".")).
  settings(
    name := "CSE511",
    version := "0.1.0",
    scalaVersion := "2.12.15",
    organization  := "org.datasyslab",
    publishMavenStyle := true,
    mainClass := Some("cse511.Main")
  )

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "3.3.1" % "provided",
  "org.apache.spark" %% "spark-sql" % "3.3.1" % "provided",
  "org.scalatest" %% "scalatest" % "3.2.12" % "test",
  "org.specs2" %% "specs2-core" % "4.15.0" % "test",
  "org.specs2" %% "specs2-junit" % "4.10.0" % "test"
)
