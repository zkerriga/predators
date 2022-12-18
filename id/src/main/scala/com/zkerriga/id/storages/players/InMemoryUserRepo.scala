package com.zkerriga.id.storages.players

import cats.syntax.either.*
import com.zkerriga.id.domain.player.{Login, PlayerId}
import com.zkerriga.id.storages.players.errors.LoginConflictError
import zio.*

class InMemoryUserRepo(private val tableRef: Ref[Map[PlayerId, Player]]) extends PlayersRepo:
  def register(player: Player): IO[LoginConflictError, Unit] =
    for
      /* todo: unsafe check, because after checking, another thread can still change the Ref */
      _ <- tableRef.get.flatMap { table =>
        if table.exists { case (_, entity) => entity.login == player.login } then
          ZIO.fail(LoginConflictError(player.login))
        else ZIO.unit
      }
      _ <- tableRef.update(_.updated(player.id, player))
      _ <- debugStateOfTable
    yield ()

  private def debugStateOfTable =
    tableRef.get.flatMap { table =>
      ZIO.logDebug(s"state of Players-Table is $table")
    }

object InMemoryUserRepo:
  val live: ULayer[PlayersRepo] =
    ZLayer.fromZIO {
      Ref.make(Map.empty[PlayerId, Player]).map(InMemoryUserRepo(_))
    }
