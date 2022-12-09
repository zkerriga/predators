package com.zkerriga.id.endpoints

import cats.syntax.either.*
import com.zkerriga.id.domain.*
import com.zkerriga.id.domain.player.*
import com.zkerriga.id.endpoints.errors.{ErrorOneOf, InternalError}
import com.zkerriga.id.endpoints.runners.{EndpointRunner, ErrorHandler}
import com.zkerriga.id.internal.domain.password.Password
import com.zkerriga.id.services.registration.RegistrationService
import com.zkerriga.id.storages.players.errors.LoginConflictError
import sttp.model.StatusCode
import sttp.tapir.*
import sttp.tapir.Codec.JsonCodec
import sttp.tapir.Schema.SName
import sttp.tapir.json.zio.*
import sttp.tapir.server.metrics.prometheus.PrometheusMetrics
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import sttp.tapir.ztapir.ZServerEndpoint
import zio.{Cause, Exit, ZIO}

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

  val protocol: PublicEndpoint[RegistrationData, LoginConflictError | InternalError, Response, Any] =
    endpoint.post
      .in("restricted" / "register" / "player")
      .in(jsonBody[RegistrationData].example(RegistrationData.Example))
      .errorOut(ErrorOneOf.genUnion[LoginConflictError, InternalError])
      .out(statusCode(StatusCode.Created))
      .out(jsonBody[Response].example(Response.Example))
      // todo: add cookie setting here for AccessToken
      .tags(List(RestrictedTag, RegistrationTag))
      .name("Register player")
      .summary("Endpoint for registering a new player")
      .description {
        """Allows you to add a new unique player to the database
          |and immediately generate them a token for authentication
          |in other services""".stripMargin
      }

  val logic: ZServerEndpoint[RegistrationService & ErrorHandler & EndpointRunner, Any] =
    protocol.serverLogic { case RegistrationData(login, password, firstName, lastName) =>
      /* todo: add logic
       * 1 - log request
       * 2 - call database and check the login there
       *   + if login exists => ConflictError
       *   + if any error with database => InternalServerError
       * 3 - call database to add new player
       *   + generate new UserId (uniq)
       *   + if any error with database => InternalServerError
       * 4 - generate new AccessToken
       * async => 5 - prepare data
       *            + set `createdAt` time
       *            + calculate `expireAt` time
       *            + get default `scopes` for player
       *          6 - try to write the data to tokens storage
       *            + if AccessToken already exists =>
       *              + remove this AccessToken
       *              + log WARN about the situation
       *            + if it is a new one => write to storage
       * 7 - log Response
       * 8 - return AccessToken to player
       */
      EndpointRunner.on[RegistrationService].apply {
        _.registerPlayer(login, password, firstName, lastName).map(Response.apply)
      }
    }
