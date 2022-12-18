package com.zkerriga.id.services.registration

import com.zkerriga.id.domain.Scopes.Scope
import com.zkerriga.id.domain.player.{Login, PlayerId}
import com.zkerriga.id.domain.{AccessToken, FirstName, LastName}
import com.zkerriga.id.internal.domain.password.Password
import com.zkerriga.id.services.generators.{TokenGenerator, UserIdGenerator}
import com.zkerriga.id.services.password.PasswordsService
import com.zkerriga.id.storages.players.errors.LoginConflictError
import com.zkerriga.id.storages.players.{Player, PlayersRepo}
import zio.{Clock, IO, Layer, ULayer, URLayer, ZIO, ZLayer}

import java.util.concurrent.TimeUnit

trait RegistrationService {
  def registerPlayer(
    login: Login,
    password: Password,
    firstName: FirstName,
    lastName: LastName,
  ): IO[LoginConflictError, AccessToken]
}

object RegistrationService:
  class Live(
    players: PlayersRepo,
    passwords: PasswordsService,
    tokenGen: TokenGenerator,
    userIdGen: UserIdGenerator,
  ) extends RegistrationService {
    def registerPlayer(
      login: Login,
      password: Password,
      firstName: FirstName,
      lastName: LastName,
    ): IO[LoginConflictError, AccessToken] =
      for
        pHash <- passwords.encrypt(password)
        id    <- userIdGen.newUserId
        now   <- Clock.instant
        entity = Player(
          id = id,
          login = login,
          pHash = pHash,
          firstName = firstName,
          lastName = lastName,
          createdAt = now,
          scopes = Set(Scope.CanOpenPlayerSocket, Scope.CanPlayPredatorsGame),
        )
        _     <- players.register(entity)
        _     <- ZIO.logInfo(s"player with id=$id successfully registered")
        token <- tokenGen.newRandomToken
      yield token // todo: other logic
  }

  val live: URLayer[PlayersRepo & PasswordsService & TokenGenerator & UserIdGenerator, RegistrationService] =
    ZLayer.fromFunction(Live(_, _, _, _))
