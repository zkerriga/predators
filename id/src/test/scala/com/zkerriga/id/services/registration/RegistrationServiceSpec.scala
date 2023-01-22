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
      testOn[RegistrationService]("happy-path") { service =>
        for {
          _      <- TestClock.setTime(currentTime)
          result <- service.registerPlayer(login, password, name, lastName)
        } yield assert(result)(equalTo(token))
      }.provide(
        MockPlayersRepo.Register(
          hasField("createdAt", (p: Player) => p.createdAt, equalTo(currentTime)),
          Expectation.unit,
        ),
        MockAccessRepo.SaveAccess(anything, Expectation.unit),
        suitLayer,
      )
    )
  )
}
