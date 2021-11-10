package services

import scala.concurrent.ExecutionContext
import play.api.cache._
import javax.inject.Inject
import models.Player
import monocle.macros.syntax.all._
import models.Hero
import repos.SpellRepo

class SpellService @Inject() (cache: AsyncCacheApi, spellRepo: SpellRepo)(implicit ec: ExecutionContext) {

  def removeActiveSpell(spellId: Int, hero: Hero): Hero = {
    spellRepo.setSpellActiveStatus(spellId, hero.id, false)
    val newHero = hero.focus(_.activeSpells).replace(hero.activeSpells.filter(s => s.id != spellId))
    newHero
  }

  def addActiveSpell(spellId: Int, hero: Hero): Either[String, Hero] = {
    if (hero.activeSpells.length >= hero.maxActiveSpells) {
      Left("No room for new active spells")
    } else {
      hero.knownSpells.find(s => s.id == spellId) match {
        case Some(spell) => {
          spellRepo.setSpellActiveStatus(spellId, hero.id, true)
          val newHero = if (hero.activeSpells.map(_.id).contains(spellId)) hero else hero.focus(_.activeSpells).replace(spell :: hero.activeSpells)
          Right(newHero)
        }
        case None => Left("Spell not known")
      }
    }
  }
}
