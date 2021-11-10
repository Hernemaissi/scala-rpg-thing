package models

import utils.Randomizer
import play.api.libs.json._

sealed trait Adventure {
  def name: String
  def possibleTroops: List[List[Enemy]]
  def possibleRewards: List[Item]

  def getRandomTroop(): List[Enemy] = Randomizer.getRandomElement(possibleTroops)
  def getRandomReward(): Item       = Randomizer.getRandomElement(possibleRewards)
}

case class Grasslands(name: String = "Grasslands", possibleTroops: List[List[Enemy]] = List(EnemyUtils.TwoBats, EnemyUtils.Bandit), possibleRewards: List[Item] = List(HealingItem(1, "Potion", "Heals for 10 hp", 10))) extends Adventure

object Grasslands {
  implicit val json = Json.format[Grasslands]
}

object Adventure {
  implicit val json = Json.format[Adventure]
}

case class AdventureStartRequest(
    adventureName: String
)

object AdventureStartRequest {
  implicit val json = Json.format[AdventureStartRequest]
}

object AdventureState extends Enumeration {
  type AdventureState = Value
  val Battle, Treasure = Value

  implicit val json = Json.formatEnum(this)
}

object BattleState extends Enumeration {
  type BattleState = Value
  val Ongoing, Won, Lost = Value

  implicit val json = Json.formatEnum(this)
}

case class Battler(
    damageTaken: Int
)

object Battler {
  implicit val json = Json.format[Battler]
}

case class Battle(
    enemies: List[Enemy],
    state: BattleState.Value
)

object Battle {
  implicit val json = Json.format[Battle]
}

sealed trait AdventureStatus {
  def state: AdventureState.Value
  def adventure: Adventure
  def stage: Int
  def bankedGold: Int
  def bankedExperience: Int
}

case class AdventureStatusBattle(
    state: AdventureState.Value,
    adventure: Adventure,
    stage: Int,
    battler: Battler,
    battle: Battle,
    bankedGold: Int,
    bankedExperience: Int
) extends AdventureStatus

case class AdventureStatusTreasure(
    state: AdventureState.Value,
    adventure: Adventure,
    stage: Int,
    bankedGold: Int,
    bankedExperience: Int
) extends AdventureStatus

object AdventureStatusBattle {
  implicit val json = Json.format[AdventureStatusBattle]
}

object AdventureStatusTreasure {
  implicit val json = Json.format[AdventureStatusTreasure]
}

object AdventureStatus {
  implicit val json = Json.format[AdventureStatus]
}

object BattleAction extends Enumeration {
  type BattleAction = Value
  val ATTACK, SPELL, DEFEND = Value

  implicit val json = Json.formatEnum(this)
}

case class AttackActionRequest(
    target: Int
)

object AttackActionRequest {
  implicit val json = Json.format[AttackActionRequest]
}

case class SpellActionRequest(
    spellName: String,
    target: Option[Int]
)

object SpellActionRequest {
  implicit val json = Json.format[SpellActionRequest]
}

case class BattleActionRequest(
    name: String,
    attackAction: Option[AttackActionRequest],
    spellAction: Option[SpellActionRequest]
)

object BattleActionRequest {
  implicit val json = Json.format[BattleActionRequest]
}

object NextRoundAction extends Enumeration {
  type NextRoundAction = Value
  val FIGHT, LEAVE = Value

  implicit val json = Json.formatEnum(this)
}

case class NextRoundActionRequest(
    action: NextRoundAction.Value
)

object NextRoundActionRequest {
  implicit val json = Json.format[NextRoundActionRequest]
}

object TreasureRoundAction extends Enumeration {
  type TreasureRoundAction = Value
  val OPEN = Value

  implicit val json = Json.formatEnum(this)
}

case class OpenTreasureActionRequest(
    action: TreasureRoundAction.Value
)

object OpenTreasureActionRequest {
  implicit val json = Json.format[OpenTreasureActionRequest]
}
