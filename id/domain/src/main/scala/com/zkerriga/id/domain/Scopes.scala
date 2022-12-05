package com.zkerriga.id.domain

object Scopes {
  opaque type Scope = String
  object Scope {
    val CanOpenPlayerSocket: Scope    = "p:socket"
    val CanOpenModeratorSocket: Scope = "m:socket"

    val CanPlayPredatorsGame: Scope = "predators"

    given CanEqual[Scope, Scope] = CanEqual.derived
  }
}
