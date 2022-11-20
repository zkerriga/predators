package com.zkerriga.id.services.registration

import com.zkerriga.id.domain.player.Login
import com.zkerriga.id.domain.{AccessToken, FirstName, LastName}
import com.zkerriga.id.internal.domain.password.Password
import com.zkerriga.id.services.password.PasswordsService
import com.zkerriga.id.services.players.PlayersService
import com.zkerriga.id.services.registration.errors.LoginConflictError
import com.zkerriga.id.services.token.TokenGenerator
import com.zkerriga.id.storages.players.PlayersRepo
import zio.{IO, Layer, ULayer, URLayer, ZIO, ZLayer}

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

  class Live(
    players: PlayersService,
    passwords: PasswordsService,
    tokenGen: TokenGenerator,
  ) extends RegistrationService {
    def registerPlayer(
      login: Login,
      password: Password,
      firstName: FirstName,
      lastName: LastName,
    ): IO[Throwable | LoginConflictError, AccessToken] =
      for
        pHash <- passwords.encrypt(password)
        id    <- players.register(login, pHash, firstName, lastName)
        _     <- ZIO.debug(s"Player with id=$id successfully registered")
        token <- tokenGen.generate
      yield token // todo: other logic
  }

  val live: URLayer[PlayersService & PasswordsService & TokenGenerator, RegistrationService] =
    ZLayer.fromFunction(Live(_, _, _))
