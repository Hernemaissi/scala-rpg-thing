package services

import models.{Adventure, Grasslands, Player, AdventureStatus, AdventureStatusBattle, AdventureState, Battle, BattleState, Town, Battler}
import play.api.cache._
import javax.inject.Inject
import scala.concurrent.ExecutionContext
import models.NextRoundAction
import monocle.macros.syntax.all._
import models.AdventureStatusTreasure
import models.Hero

//TODO: ADD USER ID TO CACHE TO AVOID CLASHING!!!!

class AdventureService @Inject() (cache: AsyncCacheApi)(implicit ec: ExecutionContext) {

  //TODO add round marker to adventure
  def startAdventure(adventureName: String, name: String): Either[String, AdventureStatus] = {
    getAdventureFromString(adventureName) match {
      case Some(value) => {
        val newAdventure: AdventureStatus = AdventureStatusBattle(
          state = AdventureState.Battle,
          adventure = value,
          stage = 1,
          bankedGold = 0,
          bankedExperience = 0,
          battler = Battler(0),
          battle = Battle(enemies = value.getRandomTroop(), state = BattleState.Ongoing)
        )
        cache.set(s"${name}-temp", newAdventure)
        Right(newAdventure)
      }
      case None => Left("Invalid adventure")
    }
  }

  def nextRoundActionHandler(choice: NextRoundAction.Value, hero: Hero, adventure: AdventureStatusBattle): Either[Hero, AdventureStatus] = {
    choice match {
      case NextRoundAction.FIGHT => {
        if (adventure.stage < 3) {
          val newAdventure = adventure.focus(_.battle).replace(Battle(enemies = adventure.adventure.getRandomTroop(), state = BattleState.Ongoing)).focus(_.stage).replace(adventure.stage + 1)
          cache.set(s"${hero.name}-temp", newAdventure)
          Right(newAdventure)
        } else {
          val newAdventure = AdventureStatusTreasure(AdventureState.Treasure, adventure = adventure.adventure, stage = 0, bankedGold = adventure.bankedGold, bankedExperience = adventure.bankedExperience)
          cache.set(s"${hero.name}-temp", newAdventure)
          Right(newAdventure)
        }

      }
      //TODO: HERE NEED TO DO A DB UPDATE
      case NextRoundAction.LEAVE => {
        cache.remove(s"${hero.name}-temp")
        var newHero = hero.focus(_.gold).replace(hero.gold + adventure.bankedGold).focus(_.location).replace(Town())
        newHero = hero.receiveExperience(adventure.bankedExperience)
        Left(newHero)
      }
    }
  }

  def openTreasure(hero: Hero, adventure: AdventureStatusTreasure): Hero = {
    var newHero = hero //InventoryService.addItemToPlayer(adventure.adventure.getRandomReward(), player).focus(_.hero.gold).replace(player.hero.gold + adventure.bankedGold)
    newHero = hero.receiveExperience(adventure.bankedExperience)
    cache.remove(s"${hero.name}-temp")
    newHero
  }

  def getAdventureFromString(adventureName: String): Option[Adventure] = {
    adventureName match {
      case "Grasslands" => Some(Grasslands())
      case _            => None
    }
  }
}
