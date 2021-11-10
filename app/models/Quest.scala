package models

import play.api.libs.json.Json

case class Quest(
    location: Location
)

object Quest {
  implicit val json = Json.format[Quest]
}
