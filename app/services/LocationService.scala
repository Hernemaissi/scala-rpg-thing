package services

import javax.inject._
import play.api.cache._
import scala.concurrent.{ExecutionContext, Future}
import models.{Player, LocationUtils, Town, Quest}

class LocationService @Inject() (cache: AsyncCacheApi, questService: QuestService)(implicit ec: ExecutionContext) {

  def travelTo(location: String, userId: String, heroId: Int): Future[Option[Quest]] = {
    questService
      .getOrException(userId, heroId)
      .map(q => {
        LocationUtils
          .travelToLocation(location, q.location) match {
          case Some(value) => questService.setLocation(value, userId, heroId).map(q => Some(q))
          case None        => Future.successful(None)
        }
      })
      .flatten
  }

  def escape(player: Player): Player = {
    player.copy(location = Town())
  }
}
