package com.zkerriga.id.storages.players

import zio.mock.{Mock, Proxy}
import zio.{IO, URLayer, ZIO, ZLayer}

// todo: find how to use @mockable[PlayersRepo] in Scala 3
object MockPlayersRepo extends Mock[PlayersRepo]:
  object Register extends Effect[Player, errors.LoginConflictError, Unit]

  val compose: URLayer[Proxy, PlayersRepo] = ZLayer.fromFunction { (proxy: Proxy) =>
    new PlayersRepo:
      def register(player: Player): IO[errors.LoginConflictError, Unit] =
        proxy(Register, player)
  }
