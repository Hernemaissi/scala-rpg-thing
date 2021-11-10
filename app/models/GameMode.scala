package models

import play.api.libs.json._

object GameMode extends Enumeration {
    type GameMode = Value
    val Travel, AdventureChoice, Battle = Value

    implicit val json = Json.formatEnum(this)
}