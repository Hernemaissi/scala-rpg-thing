package models

import play.api.libs.json._
import utils.Randomizer
import monocle.macros.syntax.all._
import play.api.Logger
import anorm._
import models.HeroClass

case class Hero(
    id: Int,
    name: String,
    heroClass: HeroClass.Value,
    level: Int,
    stats: Stats,
    gold: Int,
    inventory: Inventory,
    gear: Gear,
    knownSpells: List[SpellDao],
    activeSpells: List[SpellDao],
    maxActiveSpells: Int,
    exp: Int,
    location: Location
) {
  val logger: Logger = Logger(this.getClass())
  def getWeaponDamage() = {
    gear.weapon match {
      case None => 1
      case Some(value) => {
        inventory.items(value).item match {
          case Weapon(id, name, description, damage, itemType) => damage
          case _                                               => 1
        }
      }
    }
  }

  def calculateDamage() = {
    val weaponDamage = getWeaponDamage()
    val min          = Math.min(stats.strength, weaponDamage)
    val max          = Math.max(stats.strength, weaponDamage)
    Randomizer.getRandomIntBetween(min, max)
  }

  def calculateSpellDamage(damageEffect: DamagingEffect) = {
    val spellDamage = damageEffect.damage
    val min         = Math.min(stats.intelligence, spellDamage)
    val max         = Math.max(stats.intelligence, spellDamage)
    Randomizer.getRandomIntBetween(min, max)
  }

  def calculateDefense() = {
    calculateArmorDefense() + calculateAccessoryDefense()
  }

  //TODO, take damage in adventure only
  def takeDamage(amount: Int) = {
    this
    //this.focus(_.stats.currentHP).replace(Math.max(0, this.stats.currentHP - amount))
  }

  def receiveExperience(amount: Int) = {
    val newExp   = exp + amount
    val newLevel = levelFromExperience(newExp)
    println(s"new level is: $newLevel")
    this.focus(_.exp).replace(newExp).focus(_.level).replace(newLevel)
  }

  private def levelFromExperience(exp: Int): Int = {
    println(s"Exp is: $exp")
    exp match {
      case x if 0 to 19 contains x  => 1
      case x if 20 to 40 contains x => 2
      case _                        => 3
    }
  }

  private def calculateArmorDefense() = {
    gear.armor match {
      case None => 0
      case Some(value) => {
        inventory.items(value).item match {
          case Armor(id, name, description, defense, itemType) => defense
          case _                                               => 0
        }
      }
    }
  }

  private def calculateAccessoryDefense() = {
    gear.accessory match {
      case None => 0
      case Some(value) => {
        inventory.items(value).item match {
          case Accessory(id, name, description, defense, itemType) => defense
          case _                                                   => 0
        }
      }
    }
  }
}

object Hero {
  implicit val json = Json.format[Hero]
}

case class DatabaseHero(
    id: Int,
    name: String,
    heroClass: String,
    level: Int,
    maxHP: Int,
    maxMana: Int,
    strength: Int,
    intelligence: Int,
    constitution: Int,
    mind: Int,
    gold: Int,
    maxActiveSpells: Int,
    exp: Int,
    weapon: Option[Int],
    armor: Option[Int],
    accessory: Option[Int]
)

object DatabaseHero {
  implicit val json = Json.format[DatabaseHero]
}
