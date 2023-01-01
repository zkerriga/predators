package com.zkerriga.id.storages.tokens

import com.zkerriga.id.domain.Scopes.Scope
import com.zkerriga.id.domain.player.PlayerId
import com.zkerriga.id.domain.{AccessToken, UserId}
import com.zkerriga.id.storages.tokens.InMemoryAccessRepo.Entity
import com.zkerriga.id.storages.tokens.errors.AccessConflictError
import zio.{Clock, IO, Ref, UIO, ULayer, ZIO, ZLayer}

import java.time.Instant

private[tokens] class InMemoryAccessRepo private (
  private val tableRef: Ref[Map[AccessToken, Entity[Access]]]
) extends AccessRepo:

  def saveAccess(token: AccessToken, access: Access): IO[AccessConflictError, Unit] =
    for {
      /* todo: unsafe check, because after checking, another thread can still change the Ref */
      _ <- tableRef.get.flatMap { table =>
        if table.contains(token) then ZIO.fail(AccessConflictError(token))
        else ZIO.unit
      }
      now <- Clock.instant
      expireAt = now plusSeconds 30 // todo: get from config
      _ <- tableRef.update(_ + (token -> Entity(access, expireAt)))
    } yield ()

  def removeAccess(token: AccessToken): UIO[Unit] =
    tableRef.update(_.removed(token))

  def getAccess(token: AccessToken): UIO[Option[Access]] =
    tableRef.get.flatMap { table =>
      table.get(token).fold(ZIO.succeed(None))(ensureAccessNotExpired)
    }

  private def ensureAccessNotExpired(entity: Entity[Access]): UIO[Option[Access]] =
    Clock.instant map { now =>
      if now isBefore entity.expireAt then Some(entity.value)
      else None
    }

object InMemoryAccessRepo:
  /* todo: expiration logic would be on database side */
  private case class Entity[A](value: A, expireAt: Instant)

  val live: ULayer[AccessRepo] =
    ZLayer.fromZIO {
      Ref.make(Map.empty[AccessToken, Entity[Access]]).map(InMemoryAccessRepo(_))
    }
