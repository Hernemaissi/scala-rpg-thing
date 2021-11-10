package models
import play.api.libs.json._

case class Gear(
    weapon: Option[Int],
    armor: Option[Int],
    accessory: Option[Int]
)

object Gear {
    implicit val Gear = Json.format[Gear]
}