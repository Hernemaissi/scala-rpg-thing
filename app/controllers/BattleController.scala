package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.libs.json._
import models.{BattleActionRequest, Player, Battle, AttackActionRequest, SpellActionRequest}
import services.GameService
import scala.concurrent.{ExecutionContext, Future}
import services.BattleService
import play.api.cache._
import models.{AdventureStatusBattle, AdventureStatus}

@Singleton
class BattleController @Inject() (val controllerComponents: ControllerComponents, val battleService: BattleService, val gameService: GameService, cache: AsyncCacheApi)(implicit ec: ExecutionContext) extends BaseController {

  def action() = Action.async(parse.json) { request =>
    request.body
      .validate[BattleActionRequest]
      .fold(
        errors => {
          Future.successful(BadRequest(Json.obj("message" -> JsError.toJson(errors))))
        },
        battleActionRequest => {
          validateAction(battleActionRequest) { (player, battle) =>
            battleActionRequest match {
              case BattleActionRequest(_, Some(attackAction), None) => {
                battleService
                  .handleAttackTurn(player, battle, attackAction.target)
                  .fold(
                    errors => Future.successful(BadRequest(Json.obj("message" -> errors))),
                    afterAction => Future.successful(Ok(Json.toJson(afterAction)))
                  )
              }
              case BattleActionRequest(_, None, Some(spellAction)) => {
                battleService
                  .handleSpellTurn(player, battle, spellAction.target, spellAction.spellName)
                  .fold(
                    errors => Future.successful(BadRequest(Json.obj("message" -> errors))),
                    afterAction => Future.successful(Ok(Json.toJson(afterAction)))
                  )
              }
              case _: BattleActionRequest => Future.successful(BadRequest("Invalid battle action request"))
            }
          }
        }
      )
  }

  def validateAction(battleActionRequest: BattleActionRequest)(success: (Player, AdventureStatusBattle) => Future[Result]): Future[Result] = {
    gameService
      .loadGame(battleActionRequest.name)
      .flatMap(maybePlayer => {
        maybePlayer match {
          case Some(player) => {
            cache.get[AdventureStatus](s"${player.name}-temp").flatMap { maybeAdventure =>
              maybeAdventure match {
                case None => Future.successful(BadRequest("No adventure ongoing"))
                case Some(adventure) => {
                  adventure match {
                    case a: AdventureStatusBattle => success(player, a)
                    case _                        => Future.successful(BadRequest("Not currently fighting"))
                  }
                }
              }
            }
          }
          case None => Future.successful(BadRequest("Invalid player"))
        }
      })
  }

}
