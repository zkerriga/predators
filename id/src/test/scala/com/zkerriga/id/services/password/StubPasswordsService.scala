package com.zkerriga.id.services.password
import com.zkerriga.id.internal.domain.password.{Password, PasswordHash}
import zio.{UIO, ULayer, ZIO, ZLayer}

object StubPasswordsService extends PasswordsService:
  def encrypt(password: Password): UIO[PasswordHash] = ZIO.succeed(PasswordHash.Example)
  def verify(doubtful: Password, real: PasswordHash): UIO[Boolean] = ZIO.succeed(true)

  val stub: ULayer[PasswordsService] = ZLayer.succeed(this)
