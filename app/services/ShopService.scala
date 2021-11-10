package services

import javax.inject._
import play.api.cache._
import scala.concurrent.{ExecutionContext, Future}

import models.{Item, Player, HealingItem, Weapon, Accessory, Armor}
import scala.collection.immutable.HashMap
import repos.ShopRepo
import models.MerchantItem
import models.Hero

class ShopService @Inject() (cache: AsyncCacheApi, shopRepo: ShopRepo, inventoryService: InventoryService)(implicit ec: ExecutionContext) {

  def buy(itemId: Int, hero: Hero): Future[Either[String, Hero]] = {
    if (hero.location.name == "Shop") {
      getItemById(itemId)
        .map(maybeItem => {
          maybeItem match {
            case Some(value) => {
              if (hero.gold >= value.price) {
                inventoryService
                  .addItemToPlayer(itemId, hero)
                  .map(h => {
                    val newHero = inventoryService.removeGold(value.price, h)
                    Right(newHero)
                  })

              } else {
                Future.successful(Left("Insufficient funds"))
              }
            }
            case None => Future.successful(Left("Invalid item"))
          }
        })
        .flatten
    } else {
      Future.successful(Left("Invalid location"))
    }
  }

  def getMerchandise(merchantId: Int): Future[List[MerchantItem]] = {
    shopRepo.getMerchandise(merchantId)
  }

  private def getItemById(itemId: Int): Future[Option[MerchantItem]] = {
    //todo handle merchant id
    shopRepo.getItemById(itemId, 1)
  }
}
