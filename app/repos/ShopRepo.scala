package repos
import javax.inject.Inject
import play.api.db._
import models.{MerchantItem}
import anorm._
import anorm.SqlParser._
import scala.concurrent.{Future}

class ShopRepo @Inject() (db: Database, databaseExecutionContext: DatabaseExecutionContext) {

  implicit val ec = databaseExecutionContext

  val itemParser: RowParser[MerchantItem] = {
    get[Int]("item_id") ~
      get[String]("items.name") ~
      get[String]("description") ~
      get[Int]("price") map { case itemId ~ name ~ description ~ price =>
        MerchantItem(itemId, name, description, price)
      }
  }

  def getMerchandise(merchantId: Int): Future[List[MerchantItem]] = {
    Future {
      db.withConnection { implicit c =>
        SQL"""
        SELECT item_id, name, description, price
        FROM merchant_items
        JOIN items on merchant_items.item_id = items.id
        WHERE merchant_id = $merchantId
        """.as(itemParser.*)
      }
    }
  }

  def getItemById(itemId: Int, merchantId: Int): Future[Option[MerchantItem]] = {
    Future {
      db.withConnection { implicit c =>
        SQL"""
          SELECT item_id, name, description, price
          FROM merchant_items
          JOIN items on merchant_items.item_id = items.id
          WHERE merchant_id = $merchantId AND item_id = $itemId
          """.as(itemParser.singleOpt)
      }
    }
  }
}
