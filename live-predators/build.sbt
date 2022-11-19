val scala3Version = "3.2.1"

val catsCore       = "org.typelevel"          %% "cats-core"                  % "2.9.0"
val parCollections = "org.scala-lang.modules" %% "scala-parallel-collections" % "1.0.4"
val zioHttp        = "io.d11"                 %% "zhttp"                      % "2.0.0-RC11"

lazy val root = project
  .in(file("."))
  .settings(
    name         := "predators",
    version      := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    libraryDependencies ++= List(
      zioHttp,
      "org.scalameta" %% "munit" % "0.7.29" % Test,
    ),
  ).aggregate(simulation)

lazy val simulation = project
  .in(file("simulation"))
  .settings(
    name         := "simulation",
    scalaVersion := scala3Version,
    libraryDependencies ++= List(
      catsCore,
      parCollections,
    ),
  )
