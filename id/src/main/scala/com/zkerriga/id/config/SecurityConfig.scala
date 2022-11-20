package com.zkerriga.id.config

import com.zkerriga.id.internal.domain.password.Salt
import zio.{URLayer, ZLayer}

case class SecurityConfig(
  salt: Salt
)

object SecurityConfig:
  val live: URLayer[AppConfig, SecurityConfig] =
    ZLayer.fromFunction((config: AppConfig) => config.security)
