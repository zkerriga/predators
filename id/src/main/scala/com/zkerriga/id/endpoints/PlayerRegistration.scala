package com.zkerriga.id.endpoints

import sttp.tapir.PublicEndpoint
import com.zkerriga.id.domain.common.*
import com.zkerriga.id.domain.player.*
import sttp.tapir.*
import sttp.tapir.Codec.JsonCodec
import sttp.tapir.json.zio.*
import sttp.tapir.server.metrics.prometheus.PrometheusMetrics
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import sttp.tapir.ztapir.ZServerEndpoint
import sttp.model.StatusCode

object PlayerRegistration:
  private[endpoints] case class RegistrationRequest(
    login: Login,
    password: Password,
    firstName: FirstName,
    lastName: LastName,
  )
  object RegistrationRequest {
    val Example: RegistrationRequest =
      RegistrationRequest(Login.Example, Password.Example, FirstName.Example, LastName.Example)

    given zio.json.JsonCodec[RegistrationRequest] = zio.json.DeriveJsonCodec.gen
    given sttp.tapir.Schema[RegistrationRequest]  = Schema.derived[RegistrationRequest]
  }

  private[endpoints] case class RegistrationResponse(
    token: AccessToken
  )
  object RegistrationResponse {
    val Example: RegistrationResponse = RegistrationResponse(AccessToken.Example)

    given zio.json.JsonCodec[RegistrationResponse] = zio.json.DeriveJsonCodec.gen
    given sttp.tapir.Schema[RegistrationResponse]  = Schema.derived[RegistrationResponse]
  }

  val registerPlayer: PublicEndpoint[RegistrationRequest, Unit, RegistrationResponse, Any] =
    endpoint.post
      .in("restricted" / "register" / "player")
      .in(jsonBody[RegistrationRequest].example(RegistrationRequest.Example))
      .errorOut(statusCode(StatusCode.Conflict))
      .out(statusCode(StatusCode.Created))
      .out(jsonBody[RegistrationResponse].example(RegistrationResponse.Example))
      // todo: add cookie setting here for AccessToken
      .tags(List(Tags.Restricted, Tags.Registration))
      .name("Register player")
      .summary("Endpoint for registering a new player")
      .description {
        """Allows you to add a new unique player to the databases
          |and immediately generate him a token for authentication
          |in other services.""".stripMargin
      }

  import zio.ZIO
  val registerPlayerServerEndpoint: ZServerEndpoint[Any, Any] =
    registerPlayer.serverLogic { case RegistrationRequest(login, _, _, _) =>
      ZIO.logInfo(s"received: $login") map { _ =>
        if login == Login.Example then Right(RegistrationResponse(AccessToken.Example))
        else Left(())
      }
    }
