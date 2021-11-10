package controllers
import javax.inject._
import play.api.libs.json._
import play.api.mvc.ControllerComponents
import services.SpellService
import scala.concurrent.{ExecutionContext, Future}
import play.api.mvc.BaseController
import models.ActiveRequest
import services.GameService
import models.MageTower
import auth.HeroAction

@Singleton
class SpellController @Inject() (val controllerComponents: ControllerComponents, val spellService: SpellService, val gameService: GameService, heroAction: HeroAction)(implicit ec: ExecutionContext) extends BaseController {

  def removeActive() = heroAction(parse.json) { request =>
    request.body
      .validate[ActiveRequest]
      .fold(
        errors => BadRequest(Json.obj("message" -> JsError.toJson(errors))),
        removeRequest => {
          request.hero.location match {
            case MageTower(name, allowedTravel) =>
              Ok(Json.toJson(spellService.removeActiveSpell(removeRequest.spellId, request.hero)))
            case _ => BadRequest("Invalid location")
          }
        }
      )
  }

  def addActive() = heroAction(parse.json) { request =>
    request.body
      .validate[ActiveRequest]
      .fold(
        errors => BadRequest(Json.obj("message" -> JsError.toJson(errors))),
        addRequest => {
          request.hero.location match {
            case MageTower(name, allowedTravel) => {
              spellService
                .addActiveSpell(addRequest.spellId, request.hero)
                .fold(
                  errors => BadRequest(errors),
                  newPlayer => Ok(Json.toJson(newPlayer))
                )
            }
            case _ => BadRequest("Invalid location")
          }
        }
      )
  }
}
