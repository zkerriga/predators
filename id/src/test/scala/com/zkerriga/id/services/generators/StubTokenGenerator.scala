package com.zkerriga.id.services.generators
import com.zkerriga.id.domain.AccessToken
import zio.{UIO, ULayer, ZIO, ZLayer}

object StubTokenGenerator extends TokenGenerator:
  def newRandomToken: UIO[AccessToken] = ZIO.succeed(AccessToken.Example)

  val stub: ULayer[TokenGenerator] = ZLayer.succeed(this)
