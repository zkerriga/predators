package com.zkerriga.predators.simulation

import scala.util.Random

object PlayerLogic:
  enum Bonus:
    case Full, Nimble, Triple, Double

  def chooseBonus(using random: Random): Bonus =
    random.shuffle(Bonus.values.toList).head
