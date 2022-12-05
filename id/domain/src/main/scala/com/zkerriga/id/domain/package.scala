package com.zkerriga.id

import cats.syntax.either.*
import sttp.tapir.Schema
import zio.json.{JsonDecoder, JsonEncoder}

package object domain {
  opaque type UserId = String
  object UserId {
    /*
     * Contains only [a-z0-9]
     * The uniqueness of identifiers is based on time, which is encoded in Long. It allows us to
     * fit any value in 13 characters with the alphabet power equal to 36 (10 digits + 22 letters)
     */
    inline val Size = 13

    /* todo: move the method to special private scope */
    def fromUniqueLong(id: Long): UserId =
      java.lang.Long.toUnsignedString(id, 36).reverse.padTo(Size, '0')

    /* todo: remove from public */
    def apply(id: String): Either[String, UserId] =
      if id.length != Size then "incorrect size".asLeft
      else if id.forall(_.isLetterOrDigit) then id.asRight
      else "must contain only letters or digits".asLeft

    val Example: UserId = "w0u8qf1rig5c0"
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
    inline val Size = 50

    val AvailableChars: Set[Char] = (('0' to '9') ++ ('a' to 'z') ++ ('A' to 'Z')).toSet

    /* todo: probably just unsafe method here */
    def apply(token: String): Either[String, AccessToken] =
      if token.length != Size then "incorrect size".asLeft
      else if token.forall(AvailableChars) then token.asRight
      else "forbidden characters".asLeft

    val Example: AccessToken = "ask10adh1pa-a5alsd1-asdg14344j-faa1s-hhh19s9s7k-14"

    given JsonEncoder[AccessToken] = JsonEncoder.string
    given JsonDecoder[AccessToken] = JsonDecoder.string.mapOrFail(apply)
    given Schema[AccessToken] = Schema.string
      .description("token that allows access to other systems")
  }
}
