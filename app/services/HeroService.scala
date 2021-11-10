package services

import monocle.macros.syntax.all._
import play.api.cache._
import javax.inject.Inject
import models.HeroClass._
import models.{Player, Hero, Inventory, Stats, Gear, Town, Item}
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import models.HealingItem
import models.Weapon
import models.Accessory
import models.Armor
import repos.HeroRepo

class HeroService @Inject() (cache: AsyncCacheApi, heroRepo: HeroRepo, inventoryService: InventoryService)(implicit ec: ExecutionContext) {

  def useItem(inventoryId: Int, hero: Hero): Either[String, Hero] = {
    hero.inventory.items.find(i => i.inventoryId == inventoryId) match {
      case Some(value) =>
        value.item match {
          //TODO Drinking a healing item should only work in advneture
          case HealingItem(id, name, description, healingEffect, itemType) => {
            val newHero = inventoryService.removeItem(
              inventoryId,
              hero
            )
            Right(newHero)
          }
          case equippableItem => {
            val newHero = equipItem(inventoryId, equippableItem, hero)
            Right(newHero)
          }
        }
      case None => Left("Invalid item")
    }
  }

  def equipItem(inventoryId: Int, item: Item, hero: Hero): Hero = {
    item match {
      case Weapon(id, name, description, damage, itemType) => {
        val currentWeapon = hero.gear.weapon.getOrElse(-1)
        if (currentWeapon == inventoryId) {
          heroRepo.unequipWeapon(hero)
          hero.focus(_.gear.weapon).replace(None)
        } else {
          heroRepo.equipWeapon(inventoryId, hero)
          hero.focus(_.gear.weapon).replace(Some(inventoryId))
        }
      }
      case Accessory(id, name, description, defense, itemType) => {
        val currentAccessory = hero.gear.accessory.getOrElse(-1)
        if (currentAccessory == inventoryId) {
          heroRepo.unequipAccessory(hero)
          hero.focus(_.gear.accessory).replace(None)
        } else {
          heroRepo.equipAccessory(inventoryId, hero)
          hero.focus(_.gear.accessory).replace(Some(inventoryId))
        }
      }
      case Armor(id, name, description, defense, itemType) => {
        val currentArmor = hero.gear.armor.getOrElse(-1)
        if (currentArmor == inventoryId) {
          heroRepo.unequipArmor(hero)
          hero.focus(_.gear.armor).replace(None)
        } else {
          heroRepo.equipArmor(inventoryId, hero)
          hero.focus(_.gear.armor).replace(Some(inventoryId))
        }
      }
      case HealingItem(id, name, description, healingEffect, itemType) => {
        hero
      }
    }
  }
}
