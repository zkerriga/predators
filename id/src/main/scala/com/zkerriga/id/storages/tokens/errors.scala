package com.zkerriga.id.storages.tokens

import com.zkerriga.id.domain.AccessToken

object errors:
  case class AccessConflictError(token: AccessToken)
