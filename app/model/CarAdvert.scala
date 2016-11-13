package model

import java.time.LocalDate
import java.util.UUID

sealed trait CarAdvert {
  val id: UUID
  val title: String
  val fuel: FuelType
  val price: Int
}

object CarAdvert {
  def apply(id: UUID, title: String, fuel: FuelType, price: Int): CarAdvert = {
    require(id != null, "id must not be null")
    require(title != null && !title.isEmpty, "title must not be null or empty")
    require(fuel != null, "fuel must not be null")
    require(price >= 0, "price must not be negative")
    NewCarAdvert(id, title, fuel, price)
  }

  def apply(id: UUID, title: String, fuel: FuelType, price: Int, mileage: Int, firstRegistration: LocalDate) : CarAdvert = {
    require(id != null, "id must not be null")
    require(title != null && !title.isEmpty, "title must not be null or empty")
    require(fuel != null, "fuel must not be null")
    require(price >= 0, "price must not be negative")
    UsedCarAdvert(id, title, fuel, price, mileage, firstRegistration)
  }

  def apply(id: UUID, title: String, fuel: FuelType, price: Int, isNew: Boolean, mileage: Option[Int], firstRegistration: Option[LocalDate]) : CarAdvert = {
    if(isNew) {
      NewCarAdvert(id, title, fuel, price)
    } else {
      require(mileage.isDefined && firstRegistration.isDefined)
      UsedCarAdvert(id, title, fuel, price, mileage.get, firstRegistration.get)
    }
  }

  def unapply(carAdvert: CarAdvert) : Option[(UUID, String, FuelType, Int, Boolean, Option[Int], Option[LocalDate])] = {
    carAdvert match {
      case newCarAdvert : NewCarAdvert => Some((newCarAdvert.id, newCarAdvert.title, newCarAdvert.fuel, newCarAdvert.price, true, None, None))
      case usedCarAdvert : UsedCarAdvert => Some((usedCarAdvert.id, usedCarAdvert.title, usedCarAdvert.fuel, usedCarAdvert.price, false, Option(usedCarAdvert.mileage), Option(usedCarAdvert.firstRegistration)))
    }
  }
}

case class NewCarAdvert(id: UUID, title: String, fuel: FuelType, price: Int) extends CarAdvert {
  require(id != null, "id must not be null")
  require(title != null && !title.isEmpty, "title must not be null or empty")
  require(fuel != null, "fuel must not be null")
  require(price >= 0, "price must not be negative")
}

case class UsedCarAdvert(id: UUID, title: String, fuel: FuelType, price: Int, mileage: Int, firstRegistration: LocalDate) extends CarAdvert {
  require(id != null, "id must not be null")
  require(title != null && !title.isEmpty, "title must not be null or empty")
  require(fuel != null, "fuel must not be null")
  require(price >= 0, "price must not be negative")
  require(mileage >= 0)
  require(firstRegistration != null)
}
