package com.zkerriga.id.config

import zio.{URLayer, ZLayer}
import zio.http.ServerConfig as ZioServerConfig

case class ServerConfig(
  host: String,
  port: Int,
)

object ServerConfig:
  val live: URLayer[AppConfig, ZioServerConfig] =
    ZLayer.fromFunction { (config: AppConfig) =>
      ZioServerConfig().binding(config.server.host, config.server.port)
    }
