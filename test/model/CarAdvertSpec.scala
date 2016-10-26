package model

import java.time.LocalDate
import java.util.UUID

import org.scalatest.{FlatSpec, Matchers}

class CarAdvertSpec extends FlatSpec with Matchers {

  "CarAdvert" should "not allow negative values for price" in {
    an [IllegalArgumentException] should be thrownBy CarAdvert(UUID.randomUUID(), "advert", FuelType.GASOLINE, -1234, isNew=true, None, None)
  }

  it should "not allow mileage for new cars" in {
    an [IllegalArgumentException] should be thrownBy CarAdvert(UUID.randomUUID(), "advert", FuelType.GASOLINE, 1234, isNew=true, Some(1234), None)
  }

  it should "not allow first registration date for new cars" in {
    an [IllegalArgumentException] should be thrownBy CarAdvert(UUID.randomUUID(), "advert", FuelType.GASOLINE, 1234, isNew=true, None, Some(LocalDate.now()))
  }

  it should "not allow negative values for mileage" in {
    an [IllegalArgumentException] should be thrownBy CarAdvert(UUID.randomUUID(), "advert", FuelType.GASOLINE, -1234, isNew=false, Some(-1234), Some(LocalDate.now()))
  }
}
