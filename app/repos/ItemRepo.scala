package repos
import javax.inject.Inject
import play.api.db._
import models.{Item, Weapon, Armor, Accessory, HealingItem, InventoryItem}
import anorm._
import anorm.SqlParser._
import scala.concurrent.{Future}

case class ItemRow(
    inventoryId: Int,
    itemId: Int,
    name: String,
    description: String,
    itemType: String,
    healingEffect: Option[Int],
    damage: Option[Int],
    defense: Option[Int]
)

class ItemRepo @Inject() (db: Database, databaseExecutionContext: DatabaseExecutionContext) {

  implicit val ec = databaseExecutionContext

  val itemParser: RowParser[ItemRow] = {
    get[Int]("inventories.id") ~
      get[Int]("item_id") ~
      get[String]("items.name") ~
      get[String]("description") ~
      get[String]("item_type") ~
      get[Option[Int]]("healing_effect") ~
      get[Option[Int]]("damage") ~
      get[Option[Int]]("defense") map { case id ~ itemId ~ name ~ description ~ itemType ~ healingEffect ~ damage ~ defense =>
        ItemRow(id, itemId, name, description, itemType, healingEffect, damage, defense)
      }
  }

  def fromItemRow(itemRow: ItemRow): InventoryItem = {
    val item = itemRow match {
      case ItemRow(_, itemId, name, description, "weapon", None, Some(damage), None)     => Weapon(itemId, name, description, damage)
      case ItemRow(_, itemId, name, description, "armor", None, None, Some(defense))     => Armor(itemId, name, description, defense)
      case ItemRow(_, itemId, name, description, "accessory", None, None, Some(defense)) => Accessory(itemId, name, description, defense)
      case ItemRow(_, itemId, name, description, "healing", Some(healing), None, None)   => HealingItem(itemId, name, description, healing)
      case _                                                                             => throw new Exception(s"Invalid item model: $itemRow")
    }
    InventoryItem(itemRow.inventoryId, item)
  }

  def getInventory(userId: String, heroId: Int): Future[List[InventoryItem]] = {
    Future {
      db.withConnection { implicit c =>
        SQL"""
        SELECT inventories.id, item_id, items.name, description, item_type, healing_effect, damage, defense
        FROM inventories
        JOIN items on items.id = inventories.item_id
        JOIN heroes on heroes.id = inventories.hero_id
        WHERE inventories.hero_id = $heroId AND heroes.owner = $userId 
        """.as(itemParser.*).map(fromItemRow)
      }
    }
  }

  def getInventoryItemById(inventoryId: Int): Future[InventoryItem] = {
    Future {
      db.withConnection { implicit c =>
        SQL"""
        SELECT inventories.id, item_id, items.name, description, item_type, healing_effect, damage, defense
        FROM inventories
        JOIN items on items.id = inventories.item_id
        JOIN heroes on heroes.id = inventories.hero_id
        WHERE inventories.id = $inventoryId
        """.as(itemParser.single)
      }
    }.map(r => fromItemRow(r))
  }

  def addItemToInventory(itemId: Int, heroId: Int): Future[InventoryItem] = {
    Future {
      db.withConnection { implicit c =>
        SQL"""
          INSERT INTO inventories (hero_id, item_id)
          VALUES ($heroId, $itemId)
           """.executeInsert()
      }
    }.map(id =>
      id match {
        case None        => throw new Exception("Inserting item failed")
        case Some(value) => getInventoryItemById(value.toInt)
      }
    ).flatten
  }

  def removeItemFromInventory(inventoryId: Int, heroId: Int): Future[Boolean] = {
    Future {
      db.withConnection { implicit c =>
        SQL"""
        DELETE FROM inventories WHERE id = $inventoryId AND hero_id = $heroId
          """.execute()
      }
    }
  }
}
