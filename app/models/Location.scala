package models

import play.api.libs.json._

sealed trait Location {
  def name: String
  def allowedTravel: Seq[String]
}

case class Town(name: String = "Town", allowedTravel: Seq[String] = Seq("Shop", "Mage Tower")) extends Location
object Town { implicit val json = Json.format[Town] }
case class Shop(name: String = "Shop", allowedTravel: Seq[String] = Seq("Town")) extends Location
object Shop { implicit val json = Json.format[Shop] }
case class MageTower(name: String = "Mage Tower", allowedTravel: Seq[String] = Seq("Town")) extends Location
object MageTower { implicit val json = Json.format[MageTower] }

object Location {
  implicit val json = Json.format[Location]
}

case class TravelRequest(
    location: String
)

object TravelRequest {
  implicit val json = Json.format[TravelRequest]
}

case class EscapeRequest(
    name: String
)

object EscapeRequest {
  implicit val json = Json.format[EscapeRequest]
}

object LocationUtils {

  def travelToLocation(locationStr: String, currentLocation: Location): Option[Location] = {
    if (currentLocation.allowedTravel.contains(locationStr)) {
      getLocationFromString(locationStr)
    } else {
      None
    }
  }

  private def getLocationFromString(locationStr: String): Option[Location] = {
    locationStr match {
      case "Town"       => Some(Town())
      case "Shop"       => Some(Shop())
      case "Mage Tower" => Some(MageTower())
      case _            => None
    }
  }
}
