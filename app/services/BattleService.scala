package services

import models.{Battle, BattleAction, Player, AdventureStatusBattle}
import play.api.cache._
import javax.inject.Inject
import scala.concurrent.ExecutionContext
import monocle.Focus
import monocle.macros.syntax.all._
import models.Enemy
import models.BattleState
import play.api.Logger
import play.api.libs.json._
import models.Spell
import models.SpellTargetType

class BattleService @Inject() (cache: AsyncCacheApi)(implicit
    ec: ExecutionContext
) {
  val logger: Logger = Logger(this.getClass())

  def handlePlayerAttack(
      player: Player,
      adventure: AdventureStatusBattle,
      enemy: Enemy,
      target: Int
  ): AdventureStatusBattle = {
    val damage = player.hero.calculateDamage()
    adventure
      .focus(_.battle.enemies)
      .index(target)
      .replace(enemy.takeDamage(damage))
  }

  def handlePlayerSpell(
      player: Player,
      adventure: AdventureStatusBattle,
      target: Option[Int],
      spell: Spell
  ): Either[String, AdventureStatusBattle] = {
    var errors: List[String] = List.empty
    var updatedAdventure     = adventure
    spell.damageEffects.foreach(d => {
      val damage = player.hero.calculateSpellDamage(d)
      d.targetType match {
        case SpellTargetType.All => {
          updatedAdventure.battle.enemies.indices.foreach(i => {
            updatedAdventure = updatedAdventure
              .focus(_.battle.enemies)
              .index(i)
              .replace(updatedAdventure.battle.enemies(i).takeDamage(damage))
          })
        }
        case SpellTargetType.Single => {
          target match {
            case Some(value) => {
              updatedAdventure.battle.enemies.lift(value) match {
                case Some(enemy) => {
                  updatedAdventure = updatedAdventure
                    .focus(_.battle.enemies)
                    .index(value)
                    .replace(enemy.takeDamage(damage))
                }
                case None => errors = "Invalid Target" :: errors
              }
            }
            case None => errors = "No target for single target spell given!" :: errors
          }

        }
      }
    })
    logger.warn(s"Errors length: ${errors.length}")
    if (errors.length > 0) {
      Left(errors.mkString(","))
    } else {
      Right(updatedAdventure)
    }
  }

  def handleEnemyTurn(
      player: Player,
      adventure: AdventureStatusBattle
  ): AdventureStatusBattle = {
    var affectedPlayer    = player
    var afterPlayerAction = adventure
    afterPlayerAction.battle.enemies.filter(p => p.hp > 0).foreach { e =>
      val enemyDamage = Math.max(0, e.damage - player.hero.calculateDefense())
      affectedPlayer = player.focus(_.hero).replace(player.hero.takeDamage(enemyDamage))
      cache.set(player.name, affectedPlayer)
    }
    //TODO handle current hp in adventure only!
    if (affectedPlayer.hero.stats.maxHP == 0) {
      afterPlayerAction = afterPlayerAction.focus(_.battle.state).replace(BattleState.Lost)
    }
    if (afterPlayerAction.battle.enemies.filter(p => p.hp > 0).length == 0) {
      afterPlayerAction = afterPlayerAction.focus(_.battle.state).replace(BattleState.Won)
      afterPlayerAction = afterPlayerAction
        .focus(_.bankedGold)
        .replace(
          afterPlayerAction.bankedGold + afterPlayerAction.battle.enemies
            .map(e => e.gold)
            .reduceLeft(_ + _)
        )
      afterPlayerAction = afterPlayerAction
        .focus(_.bankedExperience)
        .replace(
          afterPlayerAction.bankedExperience + afterPlayerAction.battle.enemies
            .map(e => e.exp)
            .reduceLeft(_ + _)
        )
    }
    logger.warn(Json.toJson(afterPlayerAction).toString())
    cache.set(s"${player.name}-temp", afterPlayerAction)
    afterPlayerAction
  }
  def handleAttackTurn(
      player: Player,
      adventure: AdventureStatusBattle,
      target: Int
  ): Either[String, AdventureStatusBattle] = {
    adventure.battle.enemies.lift(target) match {
      case Some(enemy) if enemy.hp > 0 => {
        val resultString = new StringBuilder()
        var afterPlayerAction =
          handlePlayerAttack(player, adventure, enemy, target)
        afterPlayerAction = handleEnemyTurn(player, afterPlayerAction)
        Right(afterPlayerAction)
      }
      case _ => Left("Invalid enemy")
    }
  }
  //TODO HANDLE CHECKING THE SPELL EFFECT FROM DATABASE
  def handleSpellTurn(
      player: Player,
      adventure: AdventureStatusBattle,
      target: Option[Int],
      spellName: String
  ): Either[String, AdventureStatusBattle] = {
    player.hero.activeSpells.find(p => p.name == spellName) match {
      case Some(spell) => {
        /*
        handlePlayerSpell(player, adventure, target, spell).fold(
          errors => Left(errors),
          success => {
            val afterPlayerAction = handleEnemyTurn(player, success)
            Right(afterPlayerAction)
          }
        )
         */
        Left("Not implemented yet")
      }
      case None => Left("Invalid spell")
    }
  }
}
