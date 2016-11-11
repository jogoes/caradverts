package json

import java.time.LocalDate
import java.util.UUID

import model.FuelTypes._
import model.{CarAdvert, FuelType}
import play.api.libs.functional.syntax._
import play.api.libs.json._

object CarAdvertFormat {

  implicit val fuelTypeWrites = new Writes[FuelType] {
    override def writes(fuelType: FuelType): JsValue = JsString(fuelType.name)
  }

  implicit val fuelTypeReads = new Reads[FuelType] {
    override def reads(json: JsValue): JsResult[FuelType] = json match {
      case JsString(DIESEL.name) => JsSuccess(DIESEL)
      case JsString(GASOLINE.name) => JsSuccess(GASOLINE)
      case _ => JsError(s"Unknown fuel type '${json.toString}'")
    }
  }

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
