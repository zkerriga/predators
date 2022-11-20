import sbt._

object Dependencies {
  object zio {
    val version = "2.0.4"

    val core    = "dev.zio" %% "zio"      % version
    val zioJson = "dev.zio" %% "zio-json" % "0.3.0"

    val zioTest    = "dev.zio" %% "zio-test"     % version % Test
    val zioTestSbt = "dev.zio" %% "zio-test-sbt" % version % Test
  }

  object tapir {
    val version = "1.2.1"

    val zioServer  = "com.softwaremill.sttp.tapir" %% "tapir-zio-http-server"    % version
    val prometheus = "com.softwaremill.sttp.tapir" %% "tapir-prometheus-metrics" % version
    val swagger    = "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle"  % version
    val zioJson    = "com.softwaremill.sttp.tapir" %% "tapir-json-zio"           % version

    val sttpStub = "com.softwaremill.sttp.tapir" %% "tapir-sttp-stub-server" % version % Test
  }

  object cats {
    val version = "2.9.0"

    val core = "org.typelevel" %% "cats-core" % version
  }

  object other {
    val logback    = "ch.qos.logback"         % "logback-classic" % "1.4.4"
    val pureconfig = "com.github.pureconfig" %% "pureconfig-core" % "0.17.2"
  }
}
