package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.libs.json._
import scala.concurrent.{ExecutionContext, Future}
import services.{GameService, LocationService}
import models.{TravelRequest, EscapeRequest}
import views.html.defaultpages.error
import auth.HeroAction

@Singleton
class LocationController @Inject() (val controllerComponents: ControllerComponents, val locationService: LocationService, val gameService: GameService, val heroAction: HeroAction)(implicit ec: ExecutionContext) extends BaseController {

  def travel() = heroAction.async(parse.json) { request =>
    request.body
      .validate[TravelRequest]
      .fold(
        errors => Future.successful(BadRequest(Json.obj("message" -> JsError.toJson(errors)))),
        travelRequest => {
          locationService
            .travelTo(travelRequest.location, request.userId, request.hero.id)
            .map(q => {
              q match {
                case Some(value) => Ok(Json.toJson(q))
                case None        => BadRequest
              }
            })
        }
      )
  }

  def escape() = Action.async(parse.json) { request =>
    request.body
      .validate[EscapeRequest]
      .fold(
        errors => Future.successful(BadRequest(Json.obj("message" -> JsError.toJson(errors)))),
        escapeRequest => {
          gameService
            .loadGame(escapeRequest.name)
            .map(maybePlayer =>
              maybePlayer match {
                case Some(player) => Ok(Json.toJson(locationService.escape(player)))
                case None         => BadRequest("Invalid Player")
              }
            )
        }
      )
  }
}
