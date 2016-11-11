package model

sealed trait FuelType {
  val name: String
}

object FuelTypes {
  case object UNKNOWN extends FuelType {
    val name = "UNKNOWN"
  }

  case object DIESEL extends FuelType {
    val name = "DIESEL"
  }

  case object GASOLINE extends FuelType {
    val name = "GASOLINE"
  }

  def fromString(s : String) : Option[FuelType] = s match {
    case DIESEL.name => Some(DIESEL)
    case GASOLINE.name => Some(GASOLINE)
    case UNKNOWN.name => Some(UNKNOWN)
    case _ => None
  }
}

