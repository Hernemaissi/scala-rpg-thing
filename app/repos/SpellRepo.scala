package repos

import javax.inject.Inject
import play.api.db._
import anorm.RowParser
import anorm._
import anorm.SqlParser._
import models.SpellDao
import scala.concurrent.{Future}
import play.api.Logger

class SpellRepo @Inject() (db: Database, databaseExecutionContext: DatabaseExecutionContext) {
  implicit val ec    = databaseExecutionContext
  val logger: Logger = Logger(this.getClass())
  val spellParser: RowParser[SpellDao] = {
    get[Long]("spells.id") ~
      get[String]("spells.name") ~
      get[String]("spells.description") ~
      get[Int]("spells.mana_cost") ~
      get[Boolean]("known_spells.active") map { case id ~ name ~ description ~ manaCost ~ active =>
        SpellDao(id, name, description, manaCost, active)
      }
  }

  def getSpellsForHero(heroId: Int): Future[List[SpellDao]] = {
    Future {
      db.withConnection { implicit c =>
        SQL"""
                    SELECT spells.id, spells.name, spells.description, spells.mana_cost, known_spells.active
                    FROM known_spells
                    JOIN spells on spells.id = known_spells.spell_id
                    WHERE known_spells.hero_id = $heroId
                  """.as(spellParser.*)
      }
    }
  }

  def setSpellActiveStatus(spellId: Int, heroId: Int, active: Boolean): Future[Boolean] = {
    Future {
      db.withConnection { implicit c =>
        SQL"""
            UPDATE known_spells
            SET active = $active
            WHERE known_spells.hero_id = $heroId AND known_spells.spell_id = $spellId
          """.execute()
      }
    }
  }
}
