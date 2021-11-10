package services

import models.{Item, Player, InventoryItem, Hero}
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import repos.ItemRepo
import monocle.Focus
import monocle.macros.syntax.all._
import models.Inventory
import repos.HeroRepo

class InventoryService @Inject() (itemRepo: ItemRepo, heroRepo: HeroRepo)(implicit ec: ExecutionContext) {

  def removeItem(inventoryId: Int, hero: Hero): Hero = {
    itemRepo.removeItemFromInventory(inventoryId, hero.id)
    hero.focus(_.inventory).replace(Inventory(hero.inventory.items.filter(i => i.inventoryId != inventoryId)))
  }

  def addItemToPlayer(itemId: Int, hero: Hero): Future[Hero] = {
    itemRepo
      .addItemToInventory(itemId, hero.id)
      .map(addedItem => {
        val inventoryItem = InventoryItem(addedItem.inventoryId, addedItem.item)
        hero.copy(inventory = hero.inventory.copy(items = hero.inventory.items :+ inventoryItem))
      })
  }

  def removeGold(amount: Int, hero: Hero) = {
    val newHero = hero.copy(gold = hero.gold - amount)
    heroRepo.setGold(hero.id, newHero.gold)
    newHero
  }
}
