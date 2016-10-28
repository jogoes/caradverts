package json

import java.time.LocalDate
import java.util.UUID

import model.FuelType.FuelType
import model.{CarAdvert, FuelType}
import play.api.libs.functional.syntax._
import play.api.libs.json._

object CarAdvertFormat {

  implicit val fuelTypeWrites = Writes.enumNameWrites

  implicit val fuelTypeReads = Reads.enumNameReads(FuelType)

  implicit val carAdvertFormat: Format[CarAdvert] = (
      (JsPath \ "id").format[UUID] and
      (JsPath \ "title").format[String] and
      (JsPath \ "fuel").format[FuelType] and
      (JsPath \ "price").format[Int] and
      (JsPath \ "isnew").format[Boolean] and
      (JsPath \ "mileage").formatNullable[Int] and
      (JsPath \ "firstRegistration").formatNullable[LocalDate]
    ) (CarAdvert.apply, unlift(CarAdvert.unapply))
}
