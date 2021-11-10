package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.libs.json._
import models.{CreateHeroRequest, GameLoadRequest}
import services.GameService
import scala.concurrent.{ExecutionContext, Future}
import auth.AuthAction
import play.api.Logger
import repos.HeroRepo

@Singleton
class GameStateController @Inject() (val controllerComponents: ControllerComponents, val gameService: GameService, authAction: AuthAction, heroRepo: HeroRepo)(implicit ec: ExecutionContext) extends BaseController {

  val logger: Logger = Logger(this.getClass())

  def heroes() = authAction.async { request =>
    heroRepo
      .getOrInsertHeroSlots(request.userId)
      .map(slots => {
        Ok(Json.toJson(slots))
      })
  }

  def create() = authAction.async(parse.json) { request =>
    logger.warn(s"Logged in as: ${request.userId}")
    request.body
      .validate[CreateHeroRequest]
      .fold(
        errors => {
          Future.successful(BadRequest(Json.obj("message" -> JsError.toJson(errors))))
        },
        gameStartRequest => {
          gameService.createHero(request.userId, gameStartRequest.heroSlotId, gameStartRequest.name, gameStartRequest.heroClass).map(h => Ok(Json.toJson(h)))
        }
      )
  }

  def load() = authAction.async(parse.json) { request =>
    request.body
      .validate[GameLoadRequest]
      .fold(
        errors => {
          Future.successful(BadRequest(Json.obj("message" -> JsError.toJson(errors))))
        },
        gameLoadRequest => {
          gameService
            .loadHero(request.userId, gameLoadRequest.heroSlotId)
            .map(maybePlayer =>
              maybePlayer match {
                case Some(value) => Ok(Json.toJson(value))
                case None        => NotFound
              }
            )
        }
      )
  }
}
