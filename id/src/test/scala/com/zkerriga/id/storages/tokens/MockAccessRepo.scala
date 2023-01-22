package com.zkerriga.id.storages.tokens

import com.zkerriga.id.domain.AccessToken
import zio.mock.{Mock, Proxy}
import zio.{IO, UIO, URLayer, ZIO, ZLayer}

// todo: find how to use @mockable[AccessRepo] in Scala 3
object MockAccessRepo extends Mock[AccessRepo]:
  object SaveAccess   extends Effect[(AccessToken, Access), errors.AccessConflictError, Unit]
  object RemoveAccess extends Effect[AccessToken, Nothing, Unit]
  object GetAccess    extends Effect[AccessToken, Nothing, Option[Access]]

  val compose: URLayer[Proxy, AccessRepo] = ZLayer.fromFunction { (proxy: Proxy) =>
    new AccessRepo:
      def saveAccess(token: AccessToken, access: Access): IO[errors.AccessConflictError, Unit] =
        proxy(SaveAccess, token, access)

      def removeAccess(token: AccessToken): UIO[Unit] =
        proxy(RemoveAccess, token)

      def getAccess(token: AccessToken): UIO[Option[Access]] =
        proxy(GetAccess, token)
  }
