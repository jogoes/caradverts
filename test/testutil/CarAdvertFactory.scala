package testutil

import java.time.LocalDate
import java.util.UUID

import model.CarAdvert
import model.FuelType._

import scala.util.Random

object CarAdvertFactory {

  def usedCarAdvert(name: String, fuelType: FuelType) : CarAdvert = CarAdvert(UUID.randomUUID(), name, fuelType, Random.nextInt(10000000), isNew=false, Some(Random.nextInt(10000000)), Some(LocalDate.now()))

  def newCarAdvert(name: String, fuelType: FuelType) : CarAdvert = newCarAdvert(UUID.randomUUID(), name, fuelType)

  def newCarAdvert(id: UUID, name: String, fuelType: FuelType) : CarAdvert = CarAdvert(id, name, fuelType, Random.nextInt(10000000), isNew=true, None, None)
}
