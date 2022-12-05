package com.zkerriga.id.services.generators

import cats.syntax.either.*
import com.zkerriga.id.domain.AccessToken
import zio.{Chunk, Random, UIO, ULayer, ZIO, ZIOAppDefault, ZLayer}

trait TokenGenerator:
  /**
   * @note
   *   the method doesn't guarantee that generated token doesn't match the existing token
   */
  def newRandomToken: UIO[AccessToken]

object TokenGenerator:
  // todo: Naive implementation of string generation. Perhaps you should look for better options
  class Simple extends TokenGenerator {
    def newRandomToken: UIO[AccessToken] =
      for
        randomChunk <- Random.nextBytes(AccessToken.Size)
        readableString = randomChunk.map(byte2char).mkString
        token <- ZIO.fromEither(AccessToken(readableString)).mapError(RuntimeException(_)).orDie
      yield token

    private def byte2char(byte: Byte): Char =
      AvailableChars(byte.toChar % AvailableCharsSize)

    private val AvailableChars: Array[Char] = AccessToken.AvailableChars.toArray
    private val AvailableCharsSize: Int     = AvailableChars.length
  }

  val live: ULayer[TokenGenerator] =
    ZLayer.succeed(Simple())
