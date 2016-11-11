package repository.jooq

import model.{FuelType, FuelTypes}
import org.jooq.Converter

class FuelTypeConverter extends Converter[String, FuelType] {

  override def toType: Class[FuelType] = classOf[FuelType]

  override def from(s: String): FuelType = FuelTypes.fromString(s).get // TODO

  override def to(fuelType: FuelType): String = fuelType.name

  override def fromType(): Class[String] = classOf[String]
}
