package com.zkerriga.id

import cats.syntax.either.*
import sttp.tapir.Schema
import zio.json.{JsonDecoder, JsonEncoder}

package object domain {
  opaque type UserId = String
  object UserId {
    val Size = 16

    def apply(id: String): Either[String, UserId] =
      if id.length != Size then "incorrect size".asLeft
      else if id.forall(_.isLetterOrDigit) then id.asRight
      else "must contain only letters or digits".asLeft

    val Example: UserId = "de15t0dol8v1bb1"
  }

  opaque type FirstName = String
  object FirstName {
    def apply(name: String): Either[String, FirstName] =
      if name.length < 1 || name.length > 20 then "must contain from 1 to 20 characters".asLeft
      else if name.forall(_.isLetter) then name.asRight
      else "must contain only characters".asLeft

    val Example: FirstName = "Harry"

    given JsonEncoder[FirstName] = JsonEncoder.string
    given JsonDecoder[FirstName] = JsonDecoder.string.mapOrFail(apply)
    given Schema[FirstName]      = Schema.string.description("user real first name")
  }

  opaque type LastName = String
  object LastName {
    def apply(name: String): Either[String, FirstName] =
      if name.length < 1 || name.length > 20 then "must contain from 1 to 20 characters".asLeft
      else if name.forall(_.isLetter) then name.asRight
      else "must contain only characters".asLeft

    val Example: LastName = "Potter"

    given JsonEncoder[LastName] = JsonEncoder.string
    given JsonDecoder[LastName] = JsonDecoder.string.mapOrFail(apply)
    given Schema[LastName]      = Schema.string.description("user real last name")
  }

  /* Not a secure version yet */
  opaque type AccessToken = String
  object AccessToken {
    val Size = 50

    def apply(token: String): Either[String, AccessToken] =
      if token.length != Size then "incorrect size of token".asLeft
      else token.asRight

    val Example: AccessToken = "ask10adh1pa-a5alsd1-asdg14344j-faa1s-hhh19s9s7k-14"

    given JsonEncoder[AccessToken] = JsonEncoder.string
    given JsonDecoder[AccessToken] = JsonDecoder.string.mapOrFail(apply)
    given Schema[AccessToken] = Schema.string
      .description("token that allows access to other systems")
  }
}
