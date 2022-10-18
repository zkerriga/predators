package com.zkerriga.predators.simulation

/*
 * I want to see smth like this, but Scala 3 compiler cannot resolve this:
 *
 * enum Predator(target: Predator):
 *   case Snake    extends Predator(Scorpio)
 *   case Scorpio  extends Predator(Vulture)
 *   case Vulture  extends Predator(Tiger)
 *   case Tiger    extends Predator(Mongoose)
 *   case Mongoose extends Predator(Snake)
 */

sealed trait Predator {
  val target: Predator

  final def eats(another: Predator): Boolean = target == another
}

object Predator:
  case object Snake    extends Predator { val target: Predator = Scorpio  }
  case object Scorpio  extends Predator { val target: Predator = Vulture  }
  case object Vulture  extends Predator { val target: Predator = Tiger    }
  case object Tiger    extends Predator { val target: Predator = Mongoose }
  case object Mongoose extends Predator { val target: Predator = Snake    }

  val All: Set[Predator] = Set(Snake, Scorpio, Vulture, Tiger, Mongoose)
