package com.zkerriga.id

import com.zkerriga.id.config.{AppConfig, SecurityConfig}
import com.zkerriga.id.endpoints.Endpoints
import com.zkerriga.id.internal.domain.password.Salt
import com.zkerriga.id.services.password.PasswordsService
import com.zkerriga.id.services.registration.RegistrationService
import sttp.tapir.server.interceptor.log.DefaultServerLog
import sttp.tapir.server.ziohttp.{ZioHttpInterpreter, ZioHttpServerOptions}
import zhttp.http.HttpApp
import zhttp.service.server.ServerChannelFactory
import zhttp.service.{EventLoopGroup, Server, ServerChannelFactory}
import zio.*

import java.util.UUID

object Main extends ZIOAppDefault:
  type AllServices = RegistrationService

  val http: HttpApp[AllServices, Throwable] =
    ZioHttpInterpreter().toHttp(Endpoints.all)

  val server: ZIO[AppConfig & AllServices & EventLoopGroup & ServerChannelFactory & Scope, Throwable, Unit] =
    for
      config      <- ZIO.service[AppConfig]
      serverStart <- Server(http).withPort(config.server.port).make
      _ <- ZIO.logInfo(s"Go to http://localhost:${serverStart.port}/docs to open SwaggerUI")
      _ <- ZIO.never
    yield ()

  val run: ZIO[Environment with ZIOAppArgs with Scope, Any, Any] =
    server.exitCode
      .provide(
        Scope.default,
        AppConfig.live,
        EventLoopGroup.auto(),
        ServerChannelFactory.auto,
        RegistrationService.live,
        ZLayer.Debug.tree,
      )
