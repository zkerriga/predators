package com.zkerriga.id.services.registration

import com.zkerriga.id.domain.Scopes.Scope
import com.zkerriga.id.domain.player.{Login, PlayerId}
import com.zkerriga.id.domain.{AccessToken, FirstName, LastName, UserId}
import com.zkerriga.id.internal.domain.password.Password
import com.zkerriga.id.services.generators.{StubTokenGenerator, StubUserIdGenerator}
import com.zkerriga.id.services.password.StubPasswordsService
import com.zkerriga.id.storages.players.{MockPlayersRepo, Player, PlayersRepo}
import com.zkerriga.id.storages.tokens.{Access, AccessRepo, MockAccessRepo}
import com.zkerriga.id.test.utils.testOn
import zio.mock.{Expectation, MockClock}
import zio.test.*
import zio.test.Assertion.*
import zio.{RLayer, ULayer, ZIO, ZIOAppDefault, ZLayer}

import java.time.Instant

object RegistrationServiceSpec extends ZIOSpecDefault {
  private val id          = UserId.Example
  private val login       = Login.Example
  private val password    = Password.Example
  private val name        = FirstName.Example
  private val lastName    = LastName.Example
  private val token       = AccessToken.Example
  private val currentTime = Instant.ofEpochMilli(34140L)

  private val suitLayer: RLayer[AccessRepo & PlayersRepo, RegistrationService] =
    (StubTokenGenerator.stub ++ StubUserIdGenerator.stub ++ StubPasswordsService.stub) >>> RegistrationService.live

  final val spec = suite("RegistrationService")(
    suite("registerPlayer")(
      test("happy-path") {
        val registerMock = MockPlayersRepo.Register(
          hasField("createdAt", (p: Player) => p.createdAt, equalTo(currentTime)),
          Expectation.unit,
        )
        val saveAccessMock = MockAccessRepo.SaveAccess(
          equalTo(token -> Access(id, Set(Scope.CanOpenPlayerSocket, Scope.CanPlayPredatorsGame))),
          Expectation.unit,
        )
        (for {
          service <- ZIO.service[RegistrationService]
          _       <- TestClock.setTime(currentTime)
          result  <- service.registerPlayer(login, password, name, lastName)
        } yield assert(result)(equalTo(token)))
          .provide(
            registerMock,
            saveAccessMock,
            suitLayer,
          )
      }
    )
  )
}
