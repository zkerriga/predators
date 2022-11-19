package com.zkerriga.id.services.registration

import com.zkerriga.id.domain.player.Login

object errors:
  case class LoginConflictError(login: Login)
