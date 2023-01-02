package com.zkerriga.id.storages.players

import cats.syntax.either.*
import com.zkerriga.id.domain.player.{Login, PlayerId}
import com.zkerriga.id.storages.players.errors.LoginConflictError
import zio.*

private[players] class InMemoryUserRepo(private val tableRef: Ref[Map[PlayerId, Player]])
    extends PlayersRepo:

  def register(player: Player): IO[LoginConflictError, Unit] =
    for
      /* todo: ZIO.fail(...) will be calculated every time because of the eager default parameter.
       *   Will probably change it after discussion here: https://github.com/zio/zio/pull/364
       */
      ensureNoLoginConflict <- tableRef.modifySome(ZIO.fail(LoginConflictError(player.login))) {
        case table if table.forall { case (_, p) => p.login != player.login } =>
          ZIO.unit -> table.updated(player.id, player)
      }
      _ <- ensureNoLoginConflict
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
