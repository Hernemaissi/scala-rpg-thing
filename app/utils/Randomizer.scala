package utils

import scala.util.Random

object Randomizer {
    val random = new Random

    def getRandomElement[T](list: Seq[T]): T = {
        list(random.nextInt(list.length))
    }

    def getRandomIntBetween(min: Int, max: Int) = {
        min + random.nextInt((max - min) + 1)
    }
}