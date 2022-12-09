package com.zkerriga.id.endpoints.runners

import cats.syntax.either.*
import zio.*
import com.zkerriga.id.endpoints.errors.{EndpointError, InternalError}
import com.zkerriga.id.endpoints.runners
import com.zkerriga.id.storages.players.errors.LoginConflictError

trait ErrorHandler:
  def handle[R, E <: EndpointError, A](
    call: ZIO[R, E, A]
  ): URIO[R, Either[E | InternalError, A]]

object ErrorHandler:
  object Simple extends ErrorHandler {
    def handle[R, E <: EndpointError, A](
      call: ZIO[R, E, A]
    ): URIO[R, Either[E | InternalError, A]] =
      call.foldCauseZIO(
        {
          case Cause.Fail(error, _) =>
            ZIO.logWarning(
              s"Endpoint ended with error: ${error.description}"
            ) as error.asLeft
          case cause =>
            ZIO.logErrorCause(
              "Endpoint ended with unexpected error",
              cause,
            ) as InternalError.asLeft
        },
        result => ZIO.succeed(result.asRight),
      )
  }

  val live: ULayer[ErrorHandler] =
    ZLayer.succeed(Simple)
