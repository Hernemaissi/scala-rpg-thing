package services

import play.api.cache._
import javax.inject.Inject
import models.HeroClass._
import models.{Player, Hero, Inventory, Stats, Gear, Town, HeroClassHelper, DatabaseHero, Item, InventoryItem, Location}
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import models.GameMode
import repos.HeroRepo
import repos.ItemRepo
import models.SpellDao
import repos.SpellRepo
import play.api.Logger

class GameService @Inject() (cache: AsyncCacheApi, heroRepo: HeroRepo, itemRepo: ItemRepo, spellRepo: SpellRepo, questService: QuestService)(implicit ec: ExecutionContext) {
  val logger: Logger = Logger(this.getClass())

  def createHero(userId: String, heroSlotId: Int, name: String, heroClass: HeroClass): Future[Hero] = {
    for {
      databaseHero <- heroRepo.createHero(userId, heroSlotId, name, heroClass)
      inventory    <- itemRepo.getInventory(userId, databaseHero.id)
      spells       <- spellRepo.getSpellsForHero(databaseHero.id)
      quest        <- questService.getOrInitQuest(userId, databaseHero.id)
      hero         <- Future.successful(buildHero(databaseHero, inventory, spells, quest.location))
      _            <- cache.set(s"$userId-active_hero", heroSlotId)
    } yield (hero)
  }

  def loadGame(name: String): Future[Option[Player]] = {
    cache.get[Player](name)
  }

  def loadHero(userId: String, heroSlotId: Int): Future[Option[Hero]] = {
    heroRepo
      .loadHero(userId, heroSlotId)
      .map(maybehero => {
        maybehero match {
          case Some(value) => {
            for {
              inventory <- itemRepo.getInventory(userId, value.id)
              spells    <- spellRepo.getSpellsForHero(value.id)
              quest     <- questService.getOrInitQuest(userId, value.id)
              hero      <- Future.successful(buildHero(value, inventory, spells, quest.location))
              _         <- cache.set(s"$userId-active_hero", heroSlotId)
            } yield (Some(hero))
          }
          case None => Future.successful(None)
        }
      })
      .flatten
  }

  private def buildHero(databaseHero: DatabaseHero, inventory: List[InventoryItem], spells: List[SpellDao], location: Location): Hero = {
    Hero(
      id = databaseHero.id,
      name = databaseHero.name,
      heroClass = models.HeroClass.withName(databaseHero.heroClass),
      level = databaseHero.level,
      stats = Stats.statsFromDatabase(databaseHero),
      gold = databaseHero.gold,
      maxActiveSpells = databaseHero.maxActiveSpells,
      exp = databaseHero.exp,
      inventory = Inventory(inventory),
      knownSpells = spells,
      activeSpells = spells.filter(p => p.active),
      gear = Gear(databaseHero.weapon, databaseHero.armor, databaseHero.accessory),
      location = location
    )
  }
}
