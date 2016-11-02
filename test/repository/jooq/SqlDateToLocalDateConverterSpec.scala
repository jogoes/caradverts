package repository.jooq

import java.sql.Date
import java.time.LocalDate

import org.scalatest.{FlatSpec, Matchers}

class SqlDateToLocalDateConverterSpec extends FlatSpec with Matchers {

  private val converter = new SqlDateToLocalDateConverter

  "SqlDateToLocalDateConverter" should "convert SqlDate to LocalDate" in {
    val sqlDate = new Date(1477954800000L)
    converter.from(sqlDate) shouldBe LocalDate.of(2016, 11, 1)
  }

  it should "convert null LocalDate to null SqlDate" in {
    converter.from(null) shouldBe null
  }

  it should "convert LocalDate to SqlDate" in {
    converter.to(LocalDate.of(2016, 11, 1)) shouldBe new Date(1477954800000L)
  }

  it should "convert null SqlDate to null LocalDate" in {
    converter.to(null) shouldBe null
  }

}
