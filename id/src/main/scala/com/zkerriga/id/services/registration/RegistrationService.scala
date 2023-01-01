package com.zkerriga.id.services.registration

import com.zkerriga.id.domain.Scopes.Scope
import com.zkerriga.id.domain.player.{Login, PlayerId}
import com.zkerriga.id.domain.{AccessToken, FirstName, LastName, UserId}
import com.zkerriga.id.internal.domain.password.Password
import com.zkerriga.id.services.generators.{TokenGenerator, UserIdGenerator}
import com.zkerriga.id.services.password.PasswordsService
import com.zkerriga.id.storages.players.errors.LoginConflictError
import com.zkerriga.id.storages.players.{Player, PlayersRepo}
import com.zkerriga.id.storages.tokens.{Access, AccessRepo}
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
    accesses: AccessRepo,
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
        player = Player(
          id = id,
          login = login,
          pHash = pHash,
          firstName = firstName,
          lastName = lastName,
          createdAt = now,
          scopes = Set(Scope.CanOpenPlayerSocket, Scope.CanPlayPredatorsGame),
        )
        _     <- players.register(player)
        _     <- ZIO.logInfo(s"player with id=$id successfully registered")
        token <- tokenGen.newRandomToken
        _     <- saveToken(token, id, player.scopes).forkDaemon
      yield token // todo: other logic

    private def saveToken(token: AccessToken, user: UserId, scopes: Set[Scope]) =
      (
        accesses.saveAccess(token, Access(user, scopes)) *>
          ZIO.logInfo(s"access for $user was successfully saved")
      ).catchAll { _ =>
        ZIO.logError(s"token conflict, deletion of information related to: $token") *>
          accesses.removeAccess(token)
      }
  }

  val live: URLayer[PlayersRepo & AccessRepo & PasswordsService & TokenGenerator & UserIdGenerator, RegistrationService] =
    ZLayer.fromFunction(Live(_, _, _, _, _))
