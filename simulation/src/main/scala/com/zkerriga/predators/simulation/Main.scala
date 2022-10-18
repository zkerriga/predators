package com.zkerriga.predators.simulation

import cats.Semigroup
import com.zkerriga.predators.simulation.Logic.{GameState, RoundResult}

import scala.annotation.tailrec
import scala.collection.parallel.CollectionConverters.*
import scala.util.Random

object Main {
  given random: Random = Random()

  val NumberOfRounds: Long = 10000

  case class Data(
    singleFightCounter: Long = 0,
    multipleFightCounter: Long = 0,
    //
    singleLoseCounter: Long = 0,
    multipleLoseCounter: Long = 0,
    singleWinCounter: Long = 0,
    multipleWinCounter: Long = 0,
    //
    payout: BigDecimal = 0,
  )
  object Data {
    given Semigroup[Data] with {
      def combine(x: Data, y: Data): Data =
        Data(
          singleFightCounter = x.singleFightCounter + y.singleFightCounter,
          multipleFightCounter = x.multipleFightCounter + y.multipleFightCounter,
          singleLoseCounter = x.singleLoseCounter + y.singleLoseCounter,
          multipleLoseCounter = x.multipleLoseCounter + y.multipleLoseCounter,
          singleWinCounter = x.singleWinCounter + y.singleWinCounter,
          multipleWinCounter = x.multipleWinCounter + y.multipleWinCounter,
          payout = x.payout + y.payout,
        )
    }
  }

  def resultProcess(result: RoundResult): Data = {
    /* todo */
    Data()
  }

  @tailrec
  def gameRound(cards: List[Predator], state: GameState): RoundResult =
    state.add(cards.head) match
      case result: RoundResult => result
      case updated: GameState  => gameRound(cards.tail, updated)

  def main(args: Array[String]): Unit = {
    println(s"Start a simulation for $NumberOfRounds rounds")

    val combinedData: Data = (1L to NumberOfRounds).par
      .map { _ =>
        val result = gameRound(Moderator.generateCards, GameState.Empty)
        resultProcess(result)
      }.fold(Data())(summon[Semigroup[Data]].combine)

    println(s"The stimulation ended with $combinedData")
  }
}
