package com.zkerriga.id.storages.tokens

import com.zkerriga.id.domain.AccessToken
import com.zkerriga.id.storages.tokens.errors.AccessConflictError
import zio.{IO, UIO}

trait AccessRepo:
  def saveAccess(token: AccessToken, access: Access): IO[AccessConflictError, Unit]

  def removeAccess(token: AccessToken): UIO[Unit]

  def getAccess(token: AccessToken): UIO[Option[Access]]
