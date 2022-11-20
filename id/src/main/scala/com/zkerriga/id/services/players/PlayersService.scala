package com.zkerriga.id.services.players

import com.zkerriga.id.domain.Scopes.Scope
import com.zkerriga.id.domain.player.{Login, PlayerId}
import com.zkerriga.id.domain.{FirstName, LastName}
import com.zkerriga.id.internal.domain.password.PasswordHash
import com.zkerriga.id.services.registration.errors.LoginConflictError
import com.zkerriga.id.storages.players.{PlayersRepo, Player as PlayerEntity}
import zio.{Clock, IO, URLayer, ZIO, ZLayer}

trait PlayersService:
  def register(
    login: Login,
    pHash: PasswordHash,
    firstName: FirstName,
    lastName: LastName,
  ): IO[Throwable | LoginConflictError, PlayerId]

object PlayersService:
  class Live(repo: PlayersRepo) extends PlayersService {
    def register(
      login: Login,
      pHash: PasswordHash,
      firstName: FirstName,
      lastName: LastName,
    ): IO[Throwable | LoginConflictError, PlayerId] =
      for
        // todo: actually both repo calls should be in one transaction
        //   because while we are preparing the data,
        //   another thread can do the same and there will be a login conflict :(
        existed <- repo.findByLogin(login)
        _       <- existed.fold(ZIO.unit)(_ => ZIO.fail(LoginConflictError(login)))
        now     <- Clock.currentDateTime
        entity = PlayerEntity(
          login = login,
          pHash = pHash,
          firstName = firstName,
          lastName = lastName,
          createdAt = now.toInstant,
          scopes = List(Scope.CanOpenPlayerSocket, Scope.CanPlayPredatorsGame),
        )
        id <- repo.register(entity)
      yield id
  }

  val live: URLayer[PlayersRepo, PlayersService] =
    ZLayer.fromFunction(Live(_))
