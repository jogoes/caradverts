package testutil

import java.time.LocalDate
import java.util.UUID
import java.util.concurrent.ThreadLocalRandom

import model.{CarAdvert, FuelType}

object CarAdvertFactory {

  val minDay = LocalDate.of(1970, 1, 1).toEpochDay
  val maxDay = LocalDate.of(2050, 12, 31).toEpochDay

  def nextInt(min: Int, maxInclusive: Int) = ThreadLocalRandom.current().nextInt(maxInclusive + 1 - min) + min

  def randomDate = LocalDate.ofEpochDay(ThreadLocalRandom.current().nextLong(minDay, maxDay))

  def randomPrice = nextInt(0, 10000)

  def randomMileage = nextInt(0, 5000000)

  def usedCarAdvert(name: String, fuelType: FuelType): CarAdvert = CarAdvert(UUID.randomUUID(), name, fuelType, randomPrice, isNew = false, Some(randomMileage), Some(randomDate))

  def newCarAdvert(name: String, fuelType: FuelType): CarAdvert = newCarAdvert(UUID.randomUUID(), name, fuelType)

  def newCarAdvert(id: UUID, name: String, fuelType: FuelType): CarAdvert = CarAdvert(id, name, fuelType, randomPrice, isNew = true, None, None)
}
