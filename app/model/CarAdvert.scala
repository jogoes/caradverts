package model

import java.time.LocalDate
import java.util.UUID

case class CarAdvert(id: UUID, title: String, fuel: FuelType, price: Int, isNew: Boolean, mileage: Option[Int], firstRegistration: Option[LocalDate]) {
  require(id != null, "id must not be null")
  require(title != null && !title.isEmpty, "title must not be null or empty")
  require(fuel != null, "fuel must not be null")
  require(price >= 0, "price must not be negative")
  require(isNew && mileage.isEmpty && firstRegistration.isEmpty ||
    !isNew && mileage.isDefined && firstRegistration.isDefined)
  require(mileage.isEmpty || mileage.isDefined && mileage.get >= 0)
}


