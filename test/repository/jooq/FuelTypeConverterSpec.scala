package repository.jooq

import model.FuelType
import org.scalatest.{FlatSpec, Matchers}

class FuelTypeConverterSpec extends FlatSpec with Matchers {

  private val converter = new FuelTypeConverter

  "FuelTypeConverter" should "convert from string to fuel type" in {
    converter.from("GASOLINE") shouldBe FuelType.GASOLINE
    converter.from("DIESEL") shouldBe FuelType.DIESEL
  }

  it should "convert from fuel type to string" in {
    converter.to(FuelType.GASOLINE) shouldBe "GASOLINE"
    converter.to(FuelType.DIESEL) shouldBe "DIESEL"
  }

  it should "throw exception in case of invalid string" in {
    an [NoSuchElementException] should be thrownBy converter.from("XYZ")
    an [NoSuchElementException] should be thrownBy converter.from(null)
  }

  it should "throw exception in case of invalid fuel type" in {
    an [NullPointerException] should be thrownBy converter.to(null)
  }
}
