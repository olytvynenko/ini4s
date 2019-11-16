lazy val root = (project in file("."))
  .settings(
    name := "ini4s",
    version := "0.1",
    scalaVersion := "2.12.9",
    libraryDependencies ++= Seq(
      "com.google.inject" % "guice" % "4.2.2",
      "net.codingwell" %% "scala-guice" % "4.2.6",
      "org.scalatest" %% "scalatest" % "3.0.8" % Test
    )
  )