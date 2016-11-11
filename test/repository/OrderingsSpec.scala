package repository

import java.time.LocalDate
import java.util.UUID

import model.FuelType
import model.FuelTypes._
import org.scalatest.{FlatSpec, Matchers}
import repository.Orderings._

class OrderingsSpec extends FlatSpec with Matchers {

  "Orderings" should "order local date" in {
    val date1 = LocalDate.of(2016, 12, 1)
    val date2 = LocalDate.of(2015, 1, 1)
    val date3 = LocalDate.of(2016, 5, 10)

    val dates = List(date1, date2, date3)

    dates.sorted shouldBe List(date2, date3, date1)
  }

  it should "order uuids" in {
    val uuid1 = UUID.fromString("36c0c479-e881-4da4-aa82-941b18fdde92")
    val uuid2 = UUID.fromString("cdecdf53-5c61-413c-8fa5-baeb14378150")
    val uuid3 = UUID.fromString("765c4210-d462-4f6d-b913-a7023b41dbd4")

    val uuids = List(uuid1, uuid2, uuid3)
    uuids.sorted shouldBe List(uuid1, uuid3, uuid2)
  }

  it should "order fuel types" in {
    val fuelType1 = GASOLINE
    val fuelType2 = DIESEL
    val fuelType3 = GASOLINE

    val fuelTypes = List[FuelType](fuelType1, fuelType2, fuelType3)
    fuelTypes.sorted shouldBe List(fuelType2, fuelType1, fuelType3)
    fuelTypes.sorted shouldBe List(fuelType2, fuelType3, fuelType1)
  }
}
