package com.zkerriga.id.domain

import cats.syntax.either.*
import sttp.tapir.Schema
import zio.json.{JsonDecoder, JsonEncoder}

object player {
  opaque type PlayerId <: UserId = UserId
  object PlayerId {
    export UserId.Size

    val Example: PlayerId = UserId.Example

    given Conversion[UserId, PlayerId] = identity
  }

  opaque type Login = String
  object Login {
    def apply(login: String): Either[String, Login] =
      if login.length < 1 || login.length > 20 then "must contain from 1 to 20 characters".asLeft
      else if login.head.isLetter && login.tail.forall(_.isLetterOrDigit) then
        login.toLowerCase.asRight
      else "first character must be a letter, the rest - letters or digits".asLeft

    val Example: Login = "dumbledore3000"

    given JsonEncoder[Login]     = JsonEncoder.string
    given JsonDecoder[Login]     = JsonDecoder.string.mapOrFail(apply)
    given Schema[Login]          = Schema.string.description("player unique login")
    given CanEqual[Login, Login] = CanEqual.derived
  }
}
