package com.zkerriga.id.storages.players

import cats.syntax.either.*
import com.zkerriga.id.domain.player.{Login, PlayerId}
import zio.*

case class InMemoryUserRepo(table: Ref[Map[PlayerId, Player]]) extends PlayersRepo:
  def register(player: Player): Task[PlayerId] =
    for
      // todo: figure out how to generate correct strings
      randomStr <- Random
        .nextString(PlayerId.Size * 10)
        .map(_.filter(_.isLetterOrDigit).take(PlayerId.Size))
      id <- ZIO.fromEither(PlayerId(randomStr).leftMap(RuntimeException(_)))
      // todo: not safe because PlayerId is not unique
      _ <- table.update(_.updated(id, player))
      _ <- debugStateOfTable
    yield id

  def findByLogin(login: Login): Task[Option[(PlayerId, Player)]] =
    table.get.map(_.find { case (_, player) => player.login == login })

  def findById(id: PlayerId): Task[Option[Player]] =
    table.get.map(_.get(id))

  private def debugStateOfTable =
    table.get.flatMap { table =>
      ZIO.logDebug(s"The state of Players-Table is $table")
    }

object InMemoryUserRepo:
  val live: ULayer[PlayersRepo] =
    ZLayer.fromZIO {
      Ref.make(Map.empty[PlayerId, Player]).map(InMemoryUserRepo(_))
    }
