package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.libs.json._
import models.{AdventureStartRequest, NextRoundActionRequest, BattleState, AdventureStatusBattle, Player, AdventureStatus, AdventureStatusTreasure, OpenTreasureActionRequest}
import services.GameService
import scala.concurrent.{ExecutionContext, Future}
import services.AdventureService
import play.api.cache._
import auth.HeroAction
import models.Hero

@Singleton
class AdventureController @Inject() (val controllerComponents: ControllerComponents, val adventureService: AdventureService, val gameService: GameService, cache: AsyncCacheApi, heroAction: HeroAction)(implicit ec: ExecutionContext) extends BaseController {

  def start() = heroAction(parse.json) { request =>
    request.body
      .validate[AdventureStartRequest]
      .fold(
        errors => {
          BadRequest(Json.obj("message" -> JsError.toJson(errors)))
        },
        adventureStartRequest => {
          adventureService
            .startAdventure(adventureStartRequest.adventureName, request.hero.name)
            .fold(
              errors => BadRequest(Json.obj("message" -> errors)),
              adventure => Ok(Json.toJson(adventure))
            )
        }
      )
  }

  def next() = heroAction.async(parse.json) { request =>
    request.body
      .validate[NextRoundActionRequest]
      .fold(
        errors => {
          Future.successful(BadRequest(Json.obj("message" -> JsError.toJson(errors))))
        },
        nextRoundActionRequest => {
          validateNextAction(request.hero) { (hero, adventure) =>
            adventureService
              .nextRoundActionHandler(nextRoundActionRequest.action, hero, adventure)
              .fold(
                player => Future.successful(Ok(Json.toJson(hero))),
                adventure => Future.successful(Ok(Json.toJson(adventure)))
              )
          }
        }
      )
  }

  def open() = heroAction.async(parse.json) { request =>
    request.body
      .validate[OpenTreasureActionRequest]
      .fold(
        errors => Future.successful(BadRequest(Json.obj("message" -> JsError.toJson(errors)))),
        openTreasureActionRequest => {
          validateOpenAction(request.hero) { (hero, adventure) =>
            Future.successful(Ok(Json.toJson(adventureService.openTreasure(hero, adventure))))
          }
        }
      )
  }

  def validateNextAction(hero: Hero)(success: (Hero, AdventureStatusBattle) => Future[Result]): Future[Result] = {
    cache.get[AdventureStatus](s"${hero.name}-temp").flatMap { maybeAdventure =>
      maybeAdventure match {
        case None => Future.successful(BadRequest("No adventure ongoing"))
        case Some(adventure) => {
          adventure match {
            case a: AdventureStatusBattle => {
              if (a.battle.state == BattleState.Won) {
                success(hero, a)
              } else {
                Future.successful(BadRequest("Can't take adventure action currently"))
              }
            }
            case _ => Future.successful(BadRequest("Not currently fighting"))
          }
        }
      }
    }
  }

  def validateOpenAction(hero: Hero)(success: (Hero, AdventureStatusTreasure) => Future[Result]): Future[Result] = {
    cache.get[AdventureStatus](s"${hero.name}-temp").flatMap { maybeAdventure =>
      maybeAdventure match {
        case None => Future.successful(BadRequest("No adventure ongoing"))
        case Some(adventure) => {
          adventure match {
            case a: AdventureStatusTreasure => success(hero, a)
            case _                          => Future.successful(BadRequest("Not currently opening treasure"))
          }
        }
      }
    }
  }

}
