package com.zkerriga.id.storages.players

import com.zkerriga.id.domain.Scopes.Scope
import com.zkerriga.id.domain.player.{Login, PlayerId}
import com.zkerriga.id.domain.{FirstName, LastName}
import com.zkerriga.id.internal.domain.password.PasswordHash

import java.time.Instant

case class Player(
  login: Login,
  pHash: PasswordHash,
  firstName: FirstName,
  lastName: LastName,
  createdAt: Instant,
  scopes: List[Scope],
)
