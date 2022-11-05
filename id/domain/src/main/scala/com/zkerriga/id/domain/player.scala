package com.zkerriga.id.domain

import cats.syntax.either.*
import com.zkerriga.id.domain.common.{PasswordHash, UserId}
import sttp.tapir.Schema
import zio.json.{JsonDecoder, JsonEncoder}

object player {
  opaque type PlayerId <: UserId = UserId
  object PlayerId {
    def apply(id: String): Either[String, PlayerId] = UserId.apply(id)

    val Example: PlayerId = UserId.Example
  }

  opaque type Login = String
  object Login {
    def apply(login: String): Either[String, Login] =
      if login.length < 1 || login.length > 20 then "must contain from 1 to 20 characters".asLeft
      else if login.head.isLetter && login.tail.forall(_.isLetterOrDigit) then
        login.toLowerCase.asRight
      else "first character must be a letter, the rest - letters or digits".asLeft

    val Example: Login = "dumbledore3000"

    given JsonEncoder[Login] = JsonEncoder.string
    given JsonDecoder[Login] = JsonDecoder.string.mapOrFail(apply)
    given Schema[Login]      = Schema.string.description("player unique login")
  }
}
