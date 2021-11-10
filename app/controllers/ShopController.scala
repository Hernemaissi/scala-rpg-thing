package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.libs.json._
import models.{BuyRequest}
import services.{ShopService, GameService}
import scala.concurrent.{ExecutionContext, Future}
import auth.HeroAction

@Singleton
class ShopController @Inject() (val controllerComponents: ControllerComponents, val shopService: ShopService, val gameService: GameService, val heroAction: HeroAction)(implicit ec: ExecutionContext) extends BaseController {

  def buy() = heroAction.async(parse.json) { request =>
    request.body
      .validate[BuyRequest]
      .fold(
        errors => Future.successful(BadRequest(Json.obj("message" -> JsError.toJson(errors)))),
        buyRequest => {
          shopService
            .buy(buyRequest.itemId, request.hero)
            .map(r => {
              r.fold(
                errors => BadRequest(Json.obj("message" -> errors)),
                newPlayer => Ok(Json.toJson(newPlayer))
              )
            })
        }
      )
  }

  def get() = heroAction.async { request =>
    if (request.hero.location.name == "Shop") {
      //todo, handle merchandise id
      shopService
        .getMerchandise(1)
        .map(merchandise => {
          Ok(Json.toJson(merchandise))
        })
    } else {
      Future.successful(BadRequest)
    }
  }
}
