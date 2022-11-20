package com.zkerriga.id.services.token

import cats.syntax.either.*
import com.zkerriga.id.domain.AccessToken
import zio.{Random, UIO, ULayer, ZIO, ZLayer}

trait TokenGenerator:
  /**
   * @note
   *   the method doesn't guarantee that generated token doesn't match the existing token
   */
  def generate: UIO[AccessToken]

object TokenGenerator:
  class Simple extends TokenGenerator {
    def generate: UIO[AccessToken] =
      for {
        randomStr <- Random.nextString(AccessToken.Size)
        token     <- ZIO.fromEither(AccessToken(randomStr).leftMap(RuntimeException(_))).orDie
      } yield token
  }

  val live: ULayer[TokenGenerator] =
    ZLayer.succeed {
      Simple()
    }
