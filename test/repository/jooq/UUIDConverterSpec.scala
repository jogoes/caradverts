package repository.jooq

import java.util.UUID

import org.scalatest.{FlatSpec, Matchers}

class UUIDConverterSpec extends FlatSpec with Matchers {

  val converter = new UUIDConverter

  "UUIDConverter" should "convert string to UUID" in {
    converter.from("0da25f30-c6ba-41f4-a604-2c8f06d95e95") shouldBe UUID.fromString("0da25f30-c6ba-41f4-a604-2c8f06d95e95")
  }

  it should "convert UUID to string" in {
    converter.to(UUID.fromString("0da25f30-c6ba-41f4-a604-2c8f06d95e95")) shouldBe "0da25f30-c6ba-41f4-a604-2c8f06d95e95"
  }

  it should "throw exception for null parameters" in {
    an [NullPointerException] should be thrownBy converter.from(null)
    an [NullPointerException] should be thrownBy converter.to(null)
  }

  it should "throw an exception for illegal arguments" in {
    an [IllegalArgumentException] should be thrownBy converter.from("abc")
  }
}
