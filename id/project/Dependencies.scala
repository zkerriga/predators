import sbt._

object Dependencies {
  object zio {
    val version = "2.0.6"

    val core    = "dev.zio" %% "zio"      % version
    val zioJson = "dev.zio" %% "zio-json" % "0.4.2"

    val http = "io.d11" %% "zhttp" % "2.0.0-RC11"

    val kafka = "dev.zio" %% "zio-kafka" % version

    val loggingVersion = "2.1.8"

    val logging = "dev.zio" %% "zio-logging"       % loggingVersion
    val slf4j   = "dev.zio" %% "zio-logging-slf4j" % loggingVersion

    val zioTest    = "dev.zio" %% "zio-test"     % version     % Test
    val zioTestSbt = "dev.zio" %% "zio-test-sbt" % version     % Test
    val zioMock    = "dev.zio" %% "zio-mock"     % "1.0.0-RC9" % Test
  }

  object tapir {
    val version = "1.2.6"

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
    val logback    = "ch.qos.logback"           % "logback-classic" % "1.4.5"
    val pureconfig = "com.github.pureconfig"   %% "pureconfig-core" % "0.17.2"
    val idGen      = "com.softwaremill.common" %% "id-generator"    % "1.4.0"
  }
}
