import Dependencies._

val mainScalaVersion = "3.2.1"

lazy val rootProject = (project in file("."))
  .settings(
    name         := "id",
    version      := "0.1.0-SNAPSHOT",
    scalaVersion := mainScalaVersion,
    scalacOptions ++= List(
      "-language:strictEquality"
    ),
    libraryDependencies ++= List(
      tapir.zioServer,
      tapir.prometheus,
      tapir.swagger,
      tapir.zioJson,
      zio.core,
      zio.zioJson,
      zio.http,
      zio.logging,
      zio.slf4j,
      other.slf4j,
      other.pureconfig,
      other.idGen,
      // test
      tapir.sttpStub,
      zio.zioTest,
      zio.zioTestSbt,
    ),
    testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework")),
  ).dependsOn(domain)

lazy val domain = (project in file("domain")).settings(
  name         := "domain",
  scalaVersion := mainScalaVersion,
  libraryDependencies ++= List(
    zio.zioJson,
    tapir.swagger,
    cats.core,
  ),
)
