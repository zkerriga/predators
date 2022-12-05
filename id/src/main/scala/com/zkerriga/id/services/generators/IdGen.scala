package com.zkerriga.id.services.generators

import com.softwaremill.id.{IdGenerator, DefaultIdGenerator}
import zio.ZLayer

object IdGen:
  /**
   * You mustn't scale the application without changing this code! When creating more than 1
   * instance, the `workerId` must be passed
   */
  val live: ZLayer[Any, Nothing, IdGenerator] =
    ZLayer.succeed(DefaultIdGenerator())
