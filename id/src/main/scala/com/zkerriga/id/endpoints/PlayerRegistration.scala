package com.zkerriga.id.endpoints

import com.zkerriga.id.domain.common.*
import com.zkerriga.id.domain.player.*
import sttp.model.StatusCode
import sttp.tapir.Codec.JsonCodec
import sttp.tapir.*
import sttp.tapir.Schema.SName
import sttp.tapir.json.zio.*
import sttp.tapir.server.metrics.prometheus.PrometheusMetrics
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import sttp.tapir.ztapir.ZServerEndpoint

object PlayerRegistration:
  private[endpoints] case class RegistrationData(
    login: Login,
    password: Password,
    firstName: FirstName,
    lastName: LastName,
  )
  object RegistrationData {
    val Example: RegistrationData =
      RegistrationData(Login.Example, Password.Example, FirstName.Example, LastName.Example)

    given zio.json.JsonCodec[RegistrationData] = zio.json.DeriveJsonCodec.gen
    given sttp.tapir.Schema[RegistrationData] =
      Schema.derived[RegistrationData].name(SName("PlayerRegistrationData"))
  }

  private[endpoints] case class Response(
    token: AccessToken
  )
  object Response {
    val Example: Response = Response(AccessToken.Example)

    given zio.json.JsonCodec[Response] = zio.json.DeriveJsonCodec.gen
    given sttp.tapir.Schema[Response] =
      Schema.derived[Response].name(SName("PlayerRegistrationResponse"))
  }

  val registerPlayerEndpoint: PublicEndpoint[RegistrationData, Unit, Response, Any] =
    endpoint.post
      .in("restricted" / "register" / "player")
      .in(jsonBody[RegistrationData].example(RegistrationData.Example))
      .errorOut(statusCode(StatusCode.Conflict))
      .out(statusCode(StatusCode.Created))
      .out(jsonBody[Response].example(Response.Example))
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
    registerPlayerEndpoint.serverLogic { case RegistrationData(login, _, _, _) =>
      /* todo: add logic */
      ZIO.logInfo(s"received: $login") map { _ =>
        if login == Login.Example then Right(Response(AccessToken.Example))
        else Left(())
      }
    }
