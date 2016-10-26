package model

import java.time.LocalDate
import java.util.UUID

import model.FuelType.FuelType

object FuelType extends Enumeration {
  type FuelType = Value
  val GASOLINE, DIESEL = Value
}

case class CarAdvert(id: UUID, title: String, fuel: FuelType, price: Int, isNew: Boolean, mileage: Option[Int], firstRegistration: Option[LocalDate])


