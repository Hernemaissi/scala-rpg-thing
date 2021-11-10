package models
import models.HeroClass._
import play.api.libs.json._

case class Stats(
    maxHP: Int,
    maxMana: Int,
    strength: Int,
    intelligence: Int,
    constitution: Int,
    mind: Int
)

object Stats {
  def getStartingStats(heroClass: HeroClass): Stats = {
    heroClass match {
      case Warrior    => Stats(maxHP = 20, maxMana = 5, strength = 10, intelligence = 3, constitution = 8, mind = 3)
      case Wizard     => Stats(maxHP = 10, maxMana = 15, strength = 2, intelligence = 10, constitution = 3, mind = 8)
      case Spellsword => Stats(maxHP = 15, maxMana = 12, strength = 6, intelligence = 6, constitution = 6, mind = 6)
    }
  }

  def statsFromDatabase(databaseHero: DatabaseHero): Stats = {
    Stats(
      maxHP = databaseHero.maxHP,
      maxMana = databaseHero.maxMana,
      strength = databaseHero.strength,
      intelligence = databaseHero.intelligence,
      constitution = databaseHero.constitution,
      mind = databaseHero.mind
    )
  }

  implicit val json = Json.format[Stats]
}
