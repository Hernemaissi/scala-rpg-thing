package models

import play.api.libs.json._

case class Player(
    name: String,
    hero: Hero,
    location: Location,
    mode: GameMode.Value
)

object Player {
    implicit val json = Json.format[Player]
}