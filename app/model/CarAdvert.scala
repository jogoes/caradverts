package model

import java.time.LocalDate
import java.util.UUID

import model.FuelType.FuelType

object FuelType extends Enumeration {
  type FuelType = Value
  val GASOLINE, DIESEL = Value
}

case class CarAdvert(id: UUID, title: String, fuel: FuelType, price: Int, isNew: Boolean, mileage: Option[Int], firstRegistration: Option[LocalDate]) {
  require(price >= 0)
  require(isNew && mileage.isEmpty && firstRegistration.isEmpty ||
    !isNew && mileage.isDefined && firstRegistration.isDefined)
  require(mileage.isEmpty || mileage.isDefined && mileage.get >= 0)
}


