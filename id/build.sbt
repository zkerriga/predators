import Dependencies._

lazy val rootProject = (project in file(".")).settings(
  Seq(
    name         := "id",
    version      := "0.1.0-SNAPSHOT",
    scalaVersion := "3.2.0",
    libraryDependencies ++= List(
      tapir.zioServer,
      tapir.prometheus,
      tapir.swagger,
      tapir.zioJson,
      other.logback,
      zio.zioJson,
      // test
      tapir.sttpStub,
      zio.zioTest,
      zio.zioTestSbt,
    ),
    testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework")),
  )
)
