package com.zkerriga.id.config

import com.zkerriga.id.internal.domain.password.Salt

case class SecurityConfig(
  salt: Salt
)
