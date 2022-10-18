package com.zkerriga.predators.simulation

import scala.util.Random

object Moderator:
  def generateCards(using random: Random): List[Predator] =
    random.shuffle(Predator.All.toList)
