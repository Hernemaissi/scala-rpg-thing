package models
import play.api.libs.json._

sealed trait Item {
  def id: Int
  def name: String
  def description: String
  def itemType: String
}

case class HealingItem(
    override val id: Int,
    override val name: String,
    override val description: String,
    healingEffect: Int,
    override val itemType: String = "healing"
) extends Item

object HealingItem {
  def apply(id: Int, name: String, description: String, healingEffect: Int) = new HealingItem(id, name, description, healingEffect, "healing")
  implicit val json                                                         = Json.format[HealingItem]
}

case class Weapon(
    override val id: Int,
    override val name: String,
    override val description: String,
    damage: Int,
    override val itemType: String = "weapon"
) extends Item

object Weapon {
  def apply(id: Int, name: String, description: String, damage: Int) = new Weapon(id, name, description, damage, "weapon")
  implicit val json                                                  = Json.format[Weapon]
}

case class Armor(
    override val id: Int,
    override val name: String,
    override val description: String,
    defense: Int,
    override val itemType: String = "armor"
) extends Item

object Armor {
  def apply(id: Int, name: String, description: String, defense: Int) = new Armor(id, name, description, defense, "armor")
  implicit val json                                                   = Json.format[Armor]
}

case class Accessory(
    override val id: Int,
    override val name: String,
    override val description: String,
    defense: Int,
    override val itemType: String = "accessory"
) extends Item

object Accessory {
  def apply(id: Int, name: String, description: String, defense: Int) = new Accessory(id, name, description, defense, "acessory")
  implicit val json                                                   = Json.format[Accessory]
}

object Item {
  implicit val json = Json.format[Item]
}

case class InventoryItem(
    inventoryId: Int,
    item: Item
)

object InventoryItem {
  implicit val json = Json.format[InventoryItem]
}

case class UseItemRequest(
    inventoryId: Int
)

object UseItemRequest {
  implicit val json = Json.format[UseItemRequest]
}

case class MerchantItem(
    id: Int,
    name: String,
    description: String,
    price: Int
)

object MerchantItem {
  implicit val json = Json.format[MerchantItem]
}
