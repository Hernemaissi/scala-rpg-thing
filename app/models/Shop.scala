package models

import play.api.libs.json._

case class BuyRequest(
    itemId: Int
)

object BuyRequest {
  implicit val json = Json.format[BuyRequest]
}
