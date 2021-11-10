package models
import play.api.libs.json._

case class Inventory(
    items: Seq[InventoryItem]
)

object Inventory {
  implicit val json = Json.format[Inventory]
}
