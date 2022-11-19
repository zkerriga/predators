package com.zkerriga.id.internal.domain

import cats.syntax.either.*
import pureconfig.ConfigReader
import sttp.tapir.Schema
import zio.json.{JsonDecoder, JsonEncoder}

package object password {
  opaque type Password = String
  object Password {
    def apply(password: String): Either[String, Password] =
      if password.length < 8 || password.length > 40 then
        "must contain from 1 to 40 characters".asLeft
      else password.asRight

    val Example: Password = "endless-shimmering-unicorn"

    given JsonEncoder[Password] = JsonEncoder.string
    given JsonDecoder[Password] = JsonDecoder.string.mapOrFail(apply)
    given Schema[Password]      = Schema.string.description("password")
  }

  opaque type Salt = String
  object Salt {
    val Example: Salt = "4Jk7Fs0r18Rr"

    given ConfigReader[Salt] = ConfigReader.stringConfigReader
  }

  opaque type PasswordHash = String
  object PasswordHash {
    def generate[F[_]](
      password: Password,
      salt: Salt,
      hashing: String => F[String],
    ): F[PasswordHash] = hashing(s"$salt:$password")

    given CanEqual[PasswordHash, PasswordHash] = CanEqual.derived
  }
}
