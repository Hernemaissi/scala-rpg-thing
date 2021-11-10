package models

import models.HeroClass._
import play.api.libs.json._

case class CreateHeroRequest(
    heroSlotId: Int,
    name: String,
    heroClass: HeroClass
)

object CreateHeroRequest {
  implicit val json = Json.format[CreateHeroRequest]
}

case class GameLoadRequest(
    heroSlotId: Int
)

object GameLoadRequest {
  implicit val json = Json.format[GameLoadRequest]
}
