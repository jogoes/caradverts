package testutil

import java.time.LocalDate

import model.{CarAdvert, NewCarAdvert, UsedCarAdvert}

object CarAdvertHelpers {
  def toMileage(carAdvert: CarAdvert) : Int = {
    carAdvert match {
      case newCarAdvert: NewCarAdvert => Int.MinValue
      case usedCarAdvert: UsedCarAdvert => usedCarAdvert.mileage
    }
  }

  def toFirstRegistration(carAdvert: CarAdvert) : LocalDate = {
    carAdvert match {
      case newCarAdvert: NewCarAdvert => LocalDate.MIN
      case usedCarAdvert: UsedCarAdvert => usedCarAdvert.firstRegistration
    }
  }
}
