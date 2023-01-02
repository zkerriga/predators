package com.zkerriga.id.storages.players

import cats.syntax.either.*
import com.zkerriga.id.domain.player.{Login, PlayerId}
import com.zkerriga.id.storages.players.InMemoryUserRepo.Table
import com.zkerriga.id.storages.players.errors.LoginConflictError
import zio.*

private[players] class InMemoryUserRepo private (private val ref: Ref[Table]) extends PlayersRepo:

  def register(player: Player): IO[LoginConflictError, Unit] =
    for
      /* todo: ZIO.fail(...) will be calculated every time because of the eager default parameter.
       *   Will probably switch to it after discussion here: https://github.com/zio/zio/pull/364
       * ensureNoLoginConflict <- ref.modifySome(ZIO.fail(LoginConflictError(player.login))) {
       *   case Table(table, logins) if !logins(player.login) =>
       *     ZIO.unit -> Table(table.updated(player.id, player), logins + player.login)
       * }
       */
      ensureNoLoginConflict <- ref.modify { case current @ Table(table, logins) =>
        if logins(player.login) then ZIO.fail(LoginConflictError(player.login)) -> current
        else ZIO.unit -> Table(table.updated(player.id, player), logins + player.login)
      }
      _ <- ensureNoLoginConflict
      _ <- debugStateOfTable
    yield ()

  private def debugStateOfTable =
    ref.get.flatMap { table =>
      ZIO.logDebug(s"state of Players-Table is $table")
    }

object InMemoryUserRepo:
  private case class Table(table: Map[PlayerId, Player], logins: Set[Login])
  private object Table {
    val Empty: Table = Table(Map.empty, Set.empty)
  }

  val live: ULayer[PlayersRepo] =
    ZLayer.fromZIO {
      Ref.make(Table.Empty).map(InMemoryUserRepo(_))
    }
