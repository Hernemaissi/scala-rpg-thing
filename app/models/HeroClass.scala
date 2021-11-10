package models

import play.api.libs.json._

object HeroClass extends Enumeration {
  type HeroClass = Value
  val Warrior, Wizard, Spellsword = Value

  implicit val json = Json.formatEnum(this)
}

case class SpellInfo(
    knownSpells: List[Spell],
    activeSpells: List[Spell],
    maxActiveSpells: Int
)

object HeroClassHelper {
  def getSpellInfoByClass(heroClass: HeroClass.Value): SpellInfo = {
    heroClass match {
      case HeroClass.Warrior    => SpellInfo(List(), List(), 0)
      case HeroClass.Wizard     => SpellInfo(List(), List(), 3)
      case HeroClass.Spellsword => SpellInfo(List(), List(), 1)
    }
  }
}
