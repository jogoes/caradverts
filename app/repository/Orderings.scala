package repository

import java.time.LocalDate
import java.util.UUID

import model.FuelType

object Orderings {
  implicit val localDateOrdering: Ordering[LocalDate] = Ordering.by(_.toEpochDay)

  implicit val uuidOrdering: Ordering[UUID] = Ordering.by(_.toString)

  implicit val fuelOrdering: Ordering[FuelType] = Ordering.by(_.name)
}
