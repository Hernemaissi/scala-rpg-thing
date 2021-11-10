package services

import javax.inject._
import play.api.cache._
import scala.concurrent.{ExecutionContext, Future}
import models.{Quest, Location}
import models.Town

class QuestService @Inject() (cache: AsyncCacheApi)(implicit ec: ExecutionContext) {

  def getOrInitQuest(userId: String, heroId: Int): Future[Quest] = {
    cache.getOrElseUpdate(s"$userId-$heroId-quest") {
      Future.successful(Quest(Town()))
    }
  }

  def getOrException(userId: String, heroId: Int): Future[Quest] = {
    cache.get[Quest](s"$userId-$heroId-quest").map(q => q.getOrElse(throw new Exception("No quest available")))
  }

  def setLocation(location: Location, userId: String, heroId: Int): Future[Quest] = {
    cache
      .get[Quest](s"$userId-$heroId-quest")
      .map(q => {
        q match {
          case Some(value) => {
            val newQuest = value.copy(location = location)
            cache.set(s"$userId-$heroId-quest", newQuest)
            newQuest
          }
          case None => throw new Exception("No quest available")
        }
      })
  }
}
