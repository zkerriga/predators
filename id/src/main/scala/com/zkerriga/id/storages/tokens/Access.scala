package com.zkerriga.id.storages.tokens

import com.zkerriga.id.domain.Scopes.Scope
import com.zkerriga.id.domain.UserId

case class Access(
  user: UserId,
  scopes: Set[Scope],
)
