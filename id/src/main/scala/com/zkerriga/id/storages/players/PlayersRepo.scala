package com.zkerriga.id.storages.players

import com.zkerriga.id.domain.player.{Login, PlayerId}
import zio.Task

trait PlayersRepo:
  def register(player: Player): Task[PlayerId]

  def findByLogin(login: Login): Task[Option[(PlayerId, Player)]]

  def findById(id: PlayerId): Task[Option[Player]]
