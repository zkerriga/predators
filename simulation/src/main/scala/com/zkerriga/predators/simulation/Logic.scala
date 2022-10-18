package com.zkerriga.predators.simulation

object Logic:
  case class GameState(dealtPredators: List[Predator] = List.empty) {
    def add(predator: Predator): RoundResult | GameState = {
      val eat     = dealtPredators.find(dealtPredator => predator.eats(dealtPredator))
      val eatenBy = dealtPredators.find(dealtPredator => dealtPredator.eats(predator))

      (eat, eatenBy) match
        case (Some(prey), Some(winner)) => RoundResult.Batch(winner, predator, prey)
        case (Some(prey), None)         => RoundResult.SingleFight(predator, prey)
        case (None, Some(winner))       => RoundResult.SingleFight(winner, predator)
        case (None, None)               => GameState(predator :: dealtPredators)
    }
  }
  object GameState {
    val Empty: GameState = GameState()
  }

  enum RoundResult:
    case SingleFight(winner: Predator, prey: Predator)
    case Batch(winner: Predator, semiWinner: Predator, prey: Predator)
