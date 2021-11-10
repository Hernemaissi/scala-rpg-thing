package repos

import javax.inject.Inject
import scala.concurrent.{Future}
import play.api.db._
import anorm._
import play.api.libs.json._
import models.DatabaseHero
import anorm.SqlParser._
import scala.language.postfixOps
import models.HeroClass
import models.Stats
import models.HeroClassHelper
import models.Hero

case class HeroSlot(id: Int, owner: String, hero: Option[DatabaseHero])

object HeroSlot {
  implicit val json = Json.format[HeroSlot]
}

class HeroRepo @Inject() (db: Database, databaseExecutionContext: DatabaseExecutionContext) {
  implicit val ec                         = databaseExecutionContext
  val heroParser: RowParser[DatabaseHero] = Macro.namedParser[DatabaseHero](Macro.ColumnNaming.SnakeCase)
  val heroSlotParser: RowParser[HeroSlot] = {
    get[Int]("hero_slots.id") ~
      get[String]("hero_slots.owner") ~
      (heroParser ?) map { case id ~ hero_slots_owner ~ databasehero =>
        HeroSlot(id, hero_slots_owner, databasehero)
      }
  }

  def getOrInsertHeroSlots(userId: String): Future[List[HeroSlot]] = {
    val slots = Future {
      db.withConnection { implicit c =>
        SQL"SELECT * FROM hero_slots LEFT JOIN heroes ON hero_slots.id = heroes.hero_slot_id WHERE hero_slots.owner = $userId".as(heroSlotParser.*)
      }
    }
    slots
      .map(s => {
        s.length match {
          case 0 => {
            insertNewHeroSlots(userId)
          }
          case _ => slots
        }
      })
      .flatten
  }

  def insertNewHeroSlots(userId: String): Future[List[HeroSlot]] = {
    val insert = Future {
      db.withTransaction { implicit c =>
        SQL"INSERT INTO hero_slots (owner) VALUES ($userId)".execute()
        SQL"INSERT INTO hero_slots (owner) VALUES ($userId)".execute()
        SQL"INSERT INTO hero_slots (owner) VALUES ($userId)".execute()
      }
    }
    insert
      .map(_ => {
        Future {
          db.withConnection { implicit c =>
            SQL"SELECT * FROM hero_slots LEFT JOIN heroes ON hero_slots.id = heroes.hero_slot_id WHERE hero_slots.owner = $userId".as(heroSlotParser.*)
          }
        }
      })
      .flatten
  }

  def createHero(userId: String, heroSlotId: Int, name: String, heroClass: HeroClass.Value): Future[DatabaseHero] = {
    val stats  = Stats.getStartingStats(heroClass)
    val spells = HeroClassHelper.getSpellInfoByClass(heroClass)
    val insert = Future {
      db.withConnection { implicit c =>
        SQL"""INSERT INTO heroes
        (owner, hero_slot_id, name, hero_class, level, exp, gold, max_hp, max_mana, strength, intelligence, constitution, mind, max_active_spells)
        VALUES ($userId, $heroSlotId, $name, ${heroClass.toString()}, 1, 0, 150,
        ${stats.maxHP}, ${stats.maxMana}, ${stats.strength}, ${stats.intelligence}, ${stats.constitution}, ${stats.mind}, ${spells.maxActiveSpells})  
      """.executeInsert()
      }
    }
    insert.map(result => {
      DatabaseHero(
        result.get.toInt,
        name,
        heroClass.toString(),
        1,
        stats.maxHP,
        stats.maxMana,
        stats.strength,
        stats.intelligence,
        stats.constitution,
        stats.maxHP,
        150,
        spells.maxActiveSpells,
        0,
        None,
        None,
        None
      )
    })
  }

  def loadHero(userId: String, heroSlotId: Int): Future[Option[DatabaseHero]] = {
    Future {
      db.withConnection { implicit c =>
        SQL"SELECT * FROM heroes WHERE owner = $userId AND hero_slot_id = $heroSlotId".as(heroParser.singleOpt)
      }
    }
  }

  def equipWeapon(inventoryId: Int, hero: Hero): Future[Boolean] = {
    Future {
      db.withConnection { implicit c =>
        SQL"UPDATE heroes SET weapon = $inventoryId WHERE id = ${hero.id}".execute()
      }
    }
  }

  def equipArmor(inventoryId: Int, hero: Hero): Future[Boolean] = {
    Future {
      db.withConnection { implicit c =>
        SQL"UPDATE heroes SET armor = $inventoryId WHERE id = ${hero.id}".execute()
      }
    }
  }

  def equipAccessory(inventoryId: Int, hero: Hero): Future[Boolean] = {
    Future {
      db.withConnection { implicit c =>
        SQL"UPDATE heroes SET accessory = $inventoryId WHERE id = ${hero.id}".execute()
      }
    }
  }

  def unequipWeapon(hero: Hero): Future[Boolean] = {
    Future {
      db.withConnection { implicit c =>
        SQL"UPDATE heroes SET weapon = NULL WHERE id = ${hero.id}".execute()
      }
    }
  }

  def unequipArmor(hero: Hero): Future[Boolean] = {
    Future {
      db.withConnection { implicit c =>
        SQL"UPDATE heroes SET armor = NULL WHERE id = ${hero.id}".execute()
      }
    }
  }

  def unequipAccessory(hero: Hero): Future[Boolean] = {
    Future {
      db.withConnection { implicit c =>
        SQL"UPDATE heroes SET accessory = NULL WHERE id = ${hero.id}".execute()
      }
    }
  }

  def setGold(heroId: Int, newGoldAmount: Int): Future[Boolean] = {
    Future {
      db.withConnection { implicit c =>
        SQL"UPDATE heroes SET gold = $newGoldAmount WHERE id = $heroId".execute()
      }
    }
  }
}
