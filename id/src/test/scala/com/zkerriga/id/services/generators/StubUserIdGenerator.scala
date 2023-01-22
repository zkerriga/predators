package com.zkerriga.id.services.generators

import com.zkerriga.id.domain.UserId
import zio.{UIO, ULayer, ZIO, ZLayer}

object StubUserIdGenerator extends UserIdGenerator:
  def newUserId: UIO[UserId] = ZIO.succeed(UserId.Example)

  val stub: ULayer[UserIdGenerator] = ZLayer.succeed(this)
