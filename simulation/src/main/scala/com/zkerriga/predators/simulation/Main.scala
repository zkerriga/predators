package com.zkerriga.predators.simulation

import cats.Semigroup
import com.zkerriga.predators.simulation.Logic.{GameState, RoundResult}

import scala.annotation.tailrec
import scala.collection.parallel.CollectionConverters.*
import scala.util.Random

object Main {
  given random: Random = Random()

  val NumberOfRounds: Long = 1000000

  val playerGuess: Predator = Predator.Tiger
  val playerBet: BigDecimal = 1

  case class Data(
    singleFightCounter: Long = 0,
    multipleFightCounter: Long = 0,
    //
    nonFightLoseCounter: Long = 0,
    //
    singleLoseCounter: Long = 0,
    singleWinCounter: Long = 0,
    //
    multipleLoseCounter: Long = 0,
    multipleSemiWinCounter: Long = 0,
    multipleWinCounter: Long = 0,
    //
    payout: BigDecimal = 0,
  ) {
    override def toString: String =
      s"""{
         |  singleFightCounter = $singleFightCounter
         |  multipleFightCounter = $multipleFightCounter
         |  nonFightLoseCounter = $nonFightLoseCounter
         |  singleLoseCounter = $singleLoseCounter
         |  singleWinCounter = $singleWinCounter
         |  multipleLoseCounter = $multipleLoseCounter
         |  multipleSemiWinCounter = $multipleSemiWinCounter
         |  multipleWinCounter = $multipleWinCounter
         |  payout = $payout
         |}""".stripMargin
  }
  object Data {
    given Semigroup[Data] with {
      def combine(x: Data, y: Data): Data =
        Data(
          singleFightCounter = x.singleFightCounter + y.singleFightCounter,
          multipleFightCounter = x.multipleFightCounter + y.multipleFightCounter,
          nonFightLoseCounter = x.nonFightLoseCounter + y.nonFightLoseCounter,
          singleLoseCounter = x.singleLoseCounter + y.singleLoseCounter,
          singleWinCounter = x.singleWinCounter + y.singleWinCounter,
          multipleLoseCounter = x.multipleLoseCounter + y.multipleLoseCounter,
          multipleSemiWinCounter = x.multipleSemiWinCounter + y.multipleSemiWinCounter,
          multipleWinCounter = x.multipleWinCounter + y.multipleWinCounter,
          payout = x.payout + y.payout,
        )
    }
  }

  def resultProcess(result: RoundResult): Data =
    result match
      case RoundResult.SingleFight(winner, prey) =>
        Data(
          singleFightCounter = 1,
          nonFightLoseCounter = if winner != playerGuess && prey != playerGuess then 1 else 0,
          singleLoseCounter = if prey == playerGuess then 1 else 0,
          singleWinCounter = if winner == playerGuess then 1 else 0,
          payout = if winner == playerGuess then playerBet * 2 else 0,
        )
      case RoundResult.Batch(winner, semiWinner, prey) =>
        Data(
          multipleFightCounter = 1,
          nonFightLoseCounter =
            if winner != playerGuess && semiWinner != playerGuess && prey != playerGuess then 1
            else 0,
          multipleLoseCounter = if prey == playerGuess then 1 else 0,
          multipleSemiWinCounter = if semiWinner == playerGuess then 1 else 0,
          multipleWinCounter = if winner == playerGuess then 1 else 0,
          payout =
            if winner == playerGuess then playerBet * 2
            else if semiWinner == playerGuess then playerBet / 2
            else 0,
        )

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

    val rtp = combinedData.payout / (playerBet * NumberOfRounds)
    println(
      s"rtp = all payouts / all bets = ${rtp.setScale(4, BigDecimal.RoundingMode.HALF_UP)}"
    )
  }
}
