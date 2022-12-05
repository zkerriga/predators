package com.zkerriga.id.services.password

import com.zkerriga.id.config.SecurityConfig
import com.zkerriga.id.internal.domain.password.*
import zio.{UIO, URLayer, ZIO, ZLayer}

import java.security.MessageDigest

trait PasswordsService:
  def encrypt(password: Password): UIO[PasswordHash]
  def verify(doubtful: Password, real: PasswordHash): UIO[Boolean]

object PasswordsService:
  class Live(salt: Salt) extends PasswordsService {
    def encrypt(password: Password): UIO[PasswordHash] =
      PasswordHash.generate(password, salt, hashing)

    def verify(doubtful: Password, real: PasswordHash): UIO[Boolean] =
      encrypt(doubtful).map(_ == real)

    private val hashing: String => UIO[String] =
      s => ZIO.succeedBlocking(diyMD5(s))

    /* todo: the first solution from the Internet.
     *  It is necessary to find a more accurate way of hashing passwords
     */
    private def diyMD5(str: String): String =
      MessageDigest
        .getInstance("MD5")
        .digest(str.getBytes("UTF-8"))
        .map("%02x".format(_))
        .mkString("")
  }

  lazy val live: URLayer[SecurityConfig, PasswordsService] =
    ZLayer.fromFunction((config: SecurityConfig) => Live(config.salt))
