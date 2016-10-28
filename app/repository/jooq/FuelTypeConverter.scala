package repository.jooq

import model.FuelType
import org.jooq.Converter

class FuelTypeConverter extends Converter[String, FuelType.FuelType] {

  override def toType: Class[FuelType.FuelType] = classOf[FuelType.FuelType]

  override def from(s: String): FuelType.FuelType = FuelType.withName(s)

  override def to(fuelType: FuelType.FuelType): String = fuelType.toString

  override def fromType(): Class[String] = classOf[String]
}
