package model

import java.time.LocalDate
import java.util.UUID

import model.FuelTypes._
import org.scalatest.{FlatSpec, Matchers}

class CarAdvertSpec extends FlatSpec with Matchers {

  "CarAdvert" should "not allow negative values for price" in {
    an [IllegalArgumentException] should be thrownBy CarAdvert(UUID.randomUUID(), "advert", GASOLINE, -1234)
  }

  it should "not allow null for id" in {
    an [IllegalArgumentException] should be thrownBy CarAdvert(null, "advert", GASOLINE, 1234)
  }

  it should "not allow null or empty title" in {
    an [IllegalArgumentException] should be thrownBy CarAdvert(UUID.randomUUID(), null, GASOLINE, 1234)
    an [IllegalArgumentException] should be thrownBy CarAdvert(UUID.randomUUID(), "", GASOLINE, 1234)
  }

  it should "not allow null registration date" in {
    an [IllegalArgumentException] should be thrownBy CarAdvert(UUID.randomUUID(), "advert", GASOLINE, 1234, 8471, null)
  }

  it should "not allow negative values for mileage" in {
    an [IllegalArgumentException] should be thrownBy CarAdvert(UUID.randomUUID(), "advert", GASOLINE, 1234, -1234, LocalDate.now())
  }
}
