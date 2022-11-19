package com.zkerriga.id.services.registration

import com.zkerriga.id.domain.player.Login
import com.zkerriga.id.domain.{AccessToken, FirstName, LastName}
import com.zkerriga.id.internal.domain.password.Password
import com.zkerriga.id.services.registration.errors.LoginConflictError
import zio.{IO, ULayer, ZIO, ZLayer}

trait RegistrationService {
  def registerPlayer(
    login: Login,
    password: Password,
    firstName: FirstName,
    lastName: LastName,
  ): IO[Throwable | LoginConflictError, AccessToken]
}

object RegistrationService:
  def empty: RegistrationService =
    new:
      def registerPlayer(
        login: Login,
        password: Password,
        firstName: FirstName,
        lastName: LastName,
      ): IO[Throwable | LoginConflictError, AccessToken] =
        ZIO.logInfo(s"$login, $firstName, $lastName") *> ZIO.succeed(AccessToken.Example)

  val live: ULayer[RegistrationService] =
    ZLayer.succeed(empty)
