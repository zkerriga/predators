package com.zkerriga.id.storages.tokens

import com.zkerriga.id.domain.AccessToken
import com.zkerriga.id.domain.Scopes.Scope
import com.zkerriga.id.domain.UserId
import com.zkerriga.id.storages.tokens
import com.zkerriga.id.storages.tokens.errors.AccessConflictError
import com.zkerriga.id.test.utils.testOn
import zio.*
import zio.test.*
import zio.test.Assertion.*

object InMemoryAccessRepoSpec extends ZIOSpecDefault:
  private val token  = AccessToken.Example
  private val userId = UserId.Example

  final val spec = suite("InMemoryAccessRepo")(
    suite("saveAccess")(
      testOn[AccessRepo]("should fail on the same token") { repo =>
        for
          _      <- repo.saveAccess(token, Access(userId, Set.empty))
          result <- repo.saveAccess(token, Access(userId, Set.empty)).either
        yield assert(result)(isLeft(equalTo(AccessConflictError(token))))
      },
      testOn[AccessRepo]("should save access") { repo =>
        val access = Access(userId, Set(Scope.CanPlayPredatorsGame))
        for
          _      <- repo.saveAccess(token, access)
          result <- repo.getAccess(token)
        yield assert(result)(isSome(equalTo(access)))
      },
    ),
    suite("getAccess")(
      testOn[AccessRepo]("should get only unexpired accesses") { repo =>
        val access = Access(userId, Set(Scope.CanPlayPredatorsGame))
        for
          _         <- repo.saveAccess(token, access)
          _         <- TestClock.adjust(10.seconds)
          unexpired <- repo.getAccess(token)
          _         <- TestClock.adjust(1.minute)
          expired   <- repo.getAccess(token)
        yield assert(unexpired)(isSome(equalTo(access))) && assert(expired)(isNone)
      }
    ),
    suite("removeAccess")(
      testOn[AccessRepo]("should remove access") { repo =>
        for
          _      <- repo.saveAccess(token, Access(userId, Set.empty))
          _      <- repo.removeAccess(token)
          result <- repo.getAccess(token)
        yield assert(result)(isNone)
      }
    ),
  ).provideLayer(InMemoryAccessRepo.live)
