import sbt._

object Dependencies {
  object zio {
    val zioJson = "dev.zio" %% "zio-json" % "0.3.0"

    val testVersion = "2.0.2"

    val zioTest    = "dev.zio" %% "zio-test"     % testVersion % Test
    val zioTestSbt = "dev.zio" %% "zio-test-sbt" % testVersion % Test
  }

  object tapir {
    val version = "1.1.4"

    val zioServer  = "com.softwaremill.sttp.tapir" %% "tapir-zio-http-server"    % version
    val prometheus = "com.softwaremill.sttp.tapir" %% "tapir-prometheus-metrics" % version
    val swagger    = "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle"  % version
    val zioJson    = "com.softwaremill.sttp.tapir" %% "tapir-json-zio"           % version

    val sttpStub = "com.softwaremill.sttp.tapir" %% "tapir-sttp-stub-server" % version % Test
  }

  object other {
    val logback = "ch.qos.logback" % "logback-classic" % "1.4.4"
  }
}
