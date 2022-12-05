package com.zkerriga.id.services.generators

import com.softwaremill.id.IdGenerator
import com.zkerriga.id.domain.UserId
import zio.{UIO, ZIO, ZLayer, URLayer}

trait UserIdGenerator:
  def newUserId: UIO[UserId]

object UserIdGenerator:
  class Live(idGen: IdGenerator) extends UserIdGenerator {
    def newUserId: UIO[UserId] =
      for
        uniqueLongId <- ZIO.succeedBlocking(idGen.nextId())
        id = UserId.fromUniqueLong(uniqueLongId)
        _ <- ZIO.logDebug(s"new UserId generated: $id")
      yield id
  }

  val live: URLayer[IdGenerator, UserIdGenerator] =
    ZLayer.fromFunction(Live(_))
