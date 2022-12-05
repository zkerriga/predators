package com.zkerriga.id.storages.players

import com.zkerriga.id.domain.player.{Login, PlayerId}
import com.zkerriga.id.storages.players.errors.LoginConflictError
import zio.{IO, Task}

trait PlayersRepo:
  def register(player: Player): IO[LoginConflictError, Unit]
