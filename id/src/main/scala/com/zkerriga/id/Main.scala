package com.zkerriga.id

import com.zkerriga.id.Endpoints
import sttp.tapir.server.interceptor.log.DefaultServerLog
import sttp.tapir.server.ziohttp.{ZioHttpInterpreter, ZioHttpServerOptions}
import zhttp.http.HttpApp
import zhttp.service.server.ServerChannelFactory
import zhttp.service.{EventLoopGroup, Server}
import zio.logging.LogFormat.*
import zio.logging.{LogAnnotation, LogFormat, console, consoleJson}
import zio.{Console, LogLevel, Runtime, Scope, Task, ZIO, ZIOAppArgs, ZIOAppDefault, ZLayer}

import java.util.UUID

object Main extends ZIOAppDefault:
  private val traceLogAnnotation =
    LogAnnotation[String]("special-traceId", (_, value) => value, identity)

  override val bootstrap: ZLayer[ZIOAppArgs with Scope, Any, Any] =
    Runtime.removeDefaultLoggers >>> consoleJson(
      LogFormat.colored + LogFormat.annotation(traceLogAnnotation),
      logLevel = LogLevel.Debug,
    )

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    val serverOptions: ZioHttpServerOptions[Any] =
      ZioHttpServerOptions.customiseInterceptors
        .serverLog(
          DefaultServerLog[Task](
            doLogWhenReceived = msg => ZIO.logInfo(msg), // todo: remove
            doLogWhenHandled =
              (msg, error) => error.fold(ZIO.logInfo(msg))(err => ZIO.logError(s"$msg, $err")),
            doLogAllDecodeFailures =
              (msg, error) => error.fold(ZIO.logInfo(msg))(err => ZIO.logError(s"$msg, $err")),
            doLogExceptions = (msg: String, ex: Throwable) => ZIO.logError(s"$msg, $ex"),
            noLog = ZIO.unit,
          )
        )
        .metricsInterceptor(Endpoints.prometheusMetrics.metricsInterceptor())
        .options

    val app: HttpApp[Any, Throwable] = ZioHttpInterpreter(serverOptions).toHttp(Endpoints.all)

    val port = sys.env.get("http.port").map(_.toInt).getOrElse(8080)

    (for
      serverStart <- Server(app).withPort(port).make
      _ <- ZIO.logInfo(
        s"Go to http://localhost:${serverStart.port}/docs to open SwaggerUI. Press ENTER key to exit."
      ) @@ traceLogAnnotation("my-trace-id")
      _ <- Console.readLine
    yield serverStart)
      .provideSomeLayer(EventLoopGroup.auto(0) ++ ServerChannelFactory.auto ++ Scope.default)
      .exitCode
