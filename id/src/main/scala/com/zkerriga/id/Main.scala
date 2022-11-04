package com.zkerriga.id

import com.zkerriga.id.Endpoints
import org.slf4j.LoggerFactory
import sttp.tapir.server.interceptor.log.DefaultServerLog
import sttp.tapir.server.ziohttp.{ZioHttpInterpreter, ZioHttpServerOptions}
import zhttp.http.HttpApp
import zhttp.service.server.ServerChannelFactory
import zhttp.service.{EventLoopGroup, Server}
import zio.{Console, Scope, Task, ZIO, ZIOAppArgs, ZIOAppDefault}

object Main extends ZIOAppDefault:
  val log = LoggerFactory.getLogger(ZioHttpInterpreter.getClass.getName)

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    val serverOptions: ZioHttpServerOptions[Any] =
      ZioHttpServerOptions.customiseInterceptors
        .serverLog(
          DefaultServerLog[Task](
            doLogWhenReceived = msg => ZIO.succeed(log.debug(msg)), // todo: clear
            doLogWhenHandled = (msg, error) => ZIO.succeed(error.fold(log.debug(msg))(err => log.debug(msg, err))),
            doLogAllDecodeFailures = (msg, error) => ZIO.succeed(error.fold(log.debug(msg))(err => log.debug(msg, err))),
            doLogExceptions = (msg: String, ex: Throwable) => ZIO.succeed(log.debug(msg, ex)),
            noLog = ZIO.unit
          )
        )
        .metricsInterceptor(Endpoints.prometheusMetrics.metricsInterceptor())
        .options
    val app: HttpApp[Any, Throwable] = ZioHttpInterpreter(serverOptions).toHttp(Endpoints.all)

    val port = sys.env.get("http.port").map(_.toInt).getOrElse(8080)

    (for
      serverStart <- Server(app).withPort(port).make
      _ <- Console.printLine(s"Go to http://localhost:${serverStart.port}/docs to open SwaggerUI.Press ENTER key to exit.")
      _ <- Console.readLine
    yield serverStart)
      .provideSomeLayer(EventLoopGroup.auto(0) ++ ServerChannelFactory.auto ++ Scope.default)
      .exitCode
