package repository.jdbc

import java.sql.{Connection, Date, ResultSet, Types}
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

import model.FuelTypes._
import model.{CarAdvert, FuelTypes, NewCarAdvert, UsedCarAdvert}
import play.api.db.Database
import repository.{CarAdvertRepository, SortField}

class JdbcCarAdvertRepository @Inject()(db: Database) extends CarAdvertRepository {

  def toLocalDate(sqlDate: Date): Option[LocalDate] = {
    if (sqlDate == null) {
      None
    } else {
      Some(sqlDate.toLocalDate)
    }
  }

  def toCarAdvert(rs: ResultSet): CarAdvert = {
    val mileage = rs.getInt(6)
    val optMileage = if (rs.wasNull()) None else Some(mileage)
    val fuelType = FuelTypes.fromString(rs.getString(3)).getOrElse(UNKNOWN)
    val isNew = rs.getBoolean(5)
    if(isNew) {
      CarAdvert(UUID.fromString(rs.getString(1)), rs.getString(2), fuelType, rs.getInt(4))
    } else {
      CarAdvert(UUID.fromString(rs.getString(1)), rs.getString(2), fuelType, rs.getInt(4), rs.getInt(6), rs.getDate(7).toLocalDate)
    }
  }

  override def get(): Seq[CarAdvert] = {
    db.withConnection {
      connection => {
        val stmt = connection.createStatement()
        val rs = stmt.executeQuery("SELECT * FROM caradvert")

        var adverts = Seq[CarAdvert]()
        while (rs.next()) {
          val advert = toCarAdvert(rs)
          adverts = advert +: adverts
        }
        adverts
      }
    }
  }


  override def get(sortField: SortField): Seq[CarAdvert] = {
    val dbSortField = sortField.name
    db.withConnection {
      connection => {
        val stmt = connection.createStatement()
        // sort in descending order since we are prepending the items later to the returned sequence
        // and we want them in ascending order there
        val rs = stmt.executeQuery(s"SELECT * FROM caradvert ORDER BY $dbSortField DESC")

        var adverts = Seq[CarAdvert]()
        while (rs.next()) {
          val advert = toCarAdvert(rs)
          adverts = advert +: adverts
        }
        adverts
      }
    }
  }

  override def getById(id: UUID): Option[CarAdvert] = {
    db.withConnection {
      connection => {
        val stmt = connection.prepareStatement("SELECT * FROM caradvert WHERE id=?")
        stmt.setString(1, id.toString)
        val rs = stmt.executeQuery()

        rs.next match {
          case true => Some(toCarAdvert(rs))
          case _ => None
        }
      }
    }
  }

  def exists(connection: Connection, id: UUID): Boolean = {
    db.withConnection {
      connection => {
        val stmt = connection.prepareStatement("SELECT * FROM caradvert WHERE id=?")
        stmt.setString(1, id.toString)
        val rs = stmt.executeQuery()
        rs.next
      }
    }
  }

  def insert(connection: Connection, carAdvert: CarAdvert) = {
    val stmt = connection.prepareStatement("INSERT INTO caradvert (id, title, fuel, price, isnew, mileage, firstregistration) VALUES (?, ?, ?, ?, ?, ?, ?)")

    stmt.setString(1, carAdvert.id.toString)
    stmt.setString(2, carAdvert.title)
    stmt.setString(3, carAdvert.fuel.toString)
    stmt.setInt(4, carAdvert.price)
    carAdvert match {
      case newCarAdvert : NewCarAdvert =>
        stmt.setBoolean(5, true)
        stmt.setNull(6, Types.INTEGER)
        stmt.setNull(7, Types.DATE)
      case usedCarAdvert : UsedCarAdvert =>
        stmt.setBoolean(5, false)
        stmt.setInt(6, usedCarAdvert.mileage)
        stmt.setDate(7, Date.valueOf(usedCarAdvert.firstRegistration))
    }
    stmt.execute()
  }

  def add(carAdvert: CarAdvert): Boolean = {
    db.withConnection {
      connection => {
        if (exists(connection, carAdvert.id)) {
          false
        } else {
          insert(connection, carAdvert)
          true
        }
      }
    }
  }

  def update(connection: Connection, carAdvert: CarAdvert): Boolean = {
    val stmt = connection.prepareStatement("UPDATE caradvert SET title=?, fuel=?, price=?, isnew=?, mileage=?, firstregistration=? WHERE id=?")

    stmt.setString(1, carAdvert.title)
    stmt.setString(2, carAdvert.fuel.toString)
    stmt.setInt(3, carAdvert.price)
    carAdvert match {
      case newCarAdvert : NewCarAdvert =>
        stmt.setBoolean(4, true)
        stmt.setNull(5, Types.INTEGER)
        stmt.setNull(6, Types.DATE)
      case usedCarAdvert : UsedCarAdvert =>
        stmt.setBoolean(4, false)
        stmt.setInt(5, usedCarAdvert.mileage)
        stmt.setDate(6, Date.valueOf(usedCarAdvert.firstRegistration))
    }
    stmt.setString(7, carAdvert.id.toString)
    stmt.executeUpdate() > 0
  }

  def update(carAdvert: CarAdvert): Boolean = {
    db.withConnection {
      connection => {
        if (exists(connection, carAdvert.id)) {
          update(connection, carAdvert)
        } else {
          false
        }
      }
    }
  }

  def delete(id: UUID): Boolean = {
    db.withConnection {
      connection => {
        val stmt = connection.prepareStatement("DELETE FROM caradvert WHERE id=?")
        stmt.setString(1, id.toString)
        stmt.executeUpdate() > 0
      }
    }
  }
}
