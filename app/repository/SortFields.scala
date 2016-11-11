package repository

sealed trait SortField {
  val name: String
}

object SortFieldType {
  case object ID extends SortField {
    val name = "id"
  }

  case object TITLE extends SortField {
    val name = "title"
  }

  case object FUEL extends SortField {
    val name = "fuel"
  }

  case object PRICE extends SortField {
    val name = "price"
  }

  case object ISNEW extends SortField {
    val name = "isnew"
  }

  case object MILEAGE extends SortField {
    val name = "mileage"
  }

  case object FIRSTREGISTRATION extends SortField {
    val name = "firstregistration"
  }

  def fromString(s: String): Option[SortField] = Some(s.toLowerCase match {
    case ID.name => ID
    case TITLE.name => TITLE
    case FUEL.name => FUEL
    case PRICE.name => PRICE
    case ISNEW.name => ISNEW
    case MILEAGE.name => MILEAGE
    case FIRSTREGISTRATION.name => FIRSTREGISTRATION
  })
}
