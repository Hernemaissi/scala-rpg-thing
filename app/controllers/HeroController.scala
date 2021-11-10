package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.libs.json._
import models.{UseItemRequest, GameLoadRequest}
import services.GameService
import scala.concurrent.{ExecutionContext, Future}
import services.HeroService
import auth.AuthAction
import auth.HeroAction

@Singleton
class HeroController @Inject() (val controllerComponents: ControllerComponents, val heroService: HeroService, val gameService: GameService, authAction: AuthAction, heroAction: HeroAction)(implicit ec: ExecutionContext) extends BaseController {

  def useItem() = heroAction(parse.json) { request =>
    request.body
      .validate[UseItemRequest]
      .fold(
        errors => {
          BadRequest(Json.obj("message" -> JsError.toJson(errors)))
        },
        useItemRequest => {
          heroService
            .useItem(useItemRequest.inventoryId, request.hero)
            .fold(
              errors => BadRequest(Json.obj("message" -> errors)),
              result => Ok(Json.toJson(result))
            )
        }
      )
  }
}
