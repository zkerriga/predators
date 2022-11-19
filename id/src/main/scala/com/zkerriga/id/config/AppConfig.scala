package com.zkerriga.id.config

import pureconfig.*
import pureconfig.error.ConfigReaderFailures
import pureconfig.generic.derivation.default.*
import zio.{Layer, ZIO, ZLayer}

case class AppConfig(
  server: ServerConfig,
  security: SecurityConfig,
) derives ConfigReader

object AppConfig:
  val live: Layer[ConfigReaderFailures, AppConfig] =
    ZLayer.fromZIO {
      ZIO.fromEither {
        ConfigSource.default.load[AppConfig]
      }
    }
