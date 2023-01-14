import Dependencies._

ThisBuild / scalaVersion     := "2.13.8"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "ru.misis"
ThisBuild / organizationName := "misis"

val circeVersion = "0.14.1"
val akkaVersion = "2.6.18"
val akkaHttpVersion = "10.2.7"
val akkaHttpJsonVersion = "1.39.2"
lazy val slickVersion = "3.4.1"
lazy val postgresVersion = "42.5.0"

lazy val root = (project in file("."))
  .settings(
    name := "instant_payment_service",
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion,

      "com.typesafe.akka" %% "akka-actor" % akkaVersion, // модель акторов
      "com.typesafe.akka" %% "akka-stream" % akkaVersion,
      "com.typesafe.akka" %% "akka-http" % akkaHttpVersion, // http
      "de.heikoseeberger" %% "akka-http-circe" % akkaHttpJsonVersion, // либ для преобразования объектов circe в объекты akkahttp

      "com.typesafe.slick" %% "slick" % slickVersion,
      "com.typesafe.slick" %% "slick-hikaricp" % slickVersion,
      "org.postgresql" % "postgresql" % postgresVersion,

      "ch.qos.logback" % "logback-classic" % "1.2.3",

      scalaTest % Test
    )
  )

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
