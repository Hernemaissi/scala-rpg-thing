package models

import play.api.libs.json._

object SpellType extends Enumeration {
  type SpellType = Value
  val Damage, Healing = Value

  implicit val json = Json.formatEnum(this)
}

object SpellTargetType extends Enumeration {
  type SpellTargetType = Value
  val Single, All = Value

  implicit val json = Json.formatEnum(this)
}

case class DamagingEffect(
    targetType: SpellTargetType.Value,
    damage: Int
)

object DamagingEffect {
  implicit val json = Json.format[DamagingEffect]
}

case class HealingEffect(
    healing: Int
)

object HealingEffect {
  implicit val json = Json.format[HealingEffect]
}

case class Spell(
    name: String,
    description: String,
    spellType: List[SpellType.Value],
    manaCost: Int,
    damageEffects: List[DamagingEffect],
    healingEffects: List[HealingEffect]
)

object Spell {
  implicit val json = Json.format[Spell]
}

case class SpellDao(
    id: Long,
    name: String,
    description: String,
    manaCost: Int,
    active: Boolean
)

object SpellDao {
  implicit val json = Json.format[SpellDao]
}

case class ActiveRequest(
    spellId: Int
)

object ActiveRequest {
  implicit val json = Json.format[ActiveRequest]
}
