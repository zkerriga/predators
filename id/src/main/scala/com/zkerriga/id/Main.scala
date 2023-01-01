package com.zkerriga.id

import com.zkerriga.id.config.{AppConfig, SecurityConfig, ServerConfig}
import com.zkerriga.id.endpoints.Endpoints
import com.zkerriga.id.endpoints.runners.{EndpointRunner, ErrorHandler}
import com.zkerriga.id.services.generators.{IdGen, TokenGenerator, UserIdGenerator}
import com.zkerriga.id.services.password.PasswordsService
import com.zkerriga.id.services.registration.RegistrationService
import com.zkerriga.id.storages.players.InMemoryUserRepo
import com.zkerriga.id.storages.tokens.InMemoryAccessRepo
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import zio.http.{HttpApp, Server}
import zio.logging.{LogFormat, consoleJson}
import zio.{ExitCode, Runtime, Scope, ZIO, ZIOAppArgs, ZIOAppDefault, ZLayer}

object Main extends ZIOAppDefault:
  val http: HttpApp[AllServices, Throwable] =
    ZioHttpInterpreter().toHttp(Endpoints.all)

  private val serverStart: ZIO[Server & AllServices, Throwable, Unit] =
    for
      port <- Server.install[AllServices](http)
      _    <- ZIO.logInfo(s"Go to http://localhost:$port/docs to open SwaggerUI")
      _    <- ZIO.never
    yield ()

  override val bootstrap: ZLayer[ZIOAppArgs, Any, Any] =
    LoggingSetup.bootstrapLayer

  val run: ZIO[Environment & ZIOAppArgs & Scope, Any, Any] =
    serverStart.exitCode
      .provide(
        AppConfig.live,
        SecurityConfig.live,
        ServerConfig.live,
        Server.live,
        ErrorHandler.live,
        EndpointRunner.live,
        IdGen.live,
        TokenGenerator.live,
        UserIdGenerator.live,
        PasswordsService.live,
        InMemoryUserRepo.live,
        InMemoryAccessRepo.live,
        RegistrationService.live,
        ZLayer.Debug.tree,
      )
