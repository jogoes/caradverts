package repository.jooq

import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

import generated.jooq.tables.Caradvert._
import model.{CarAdvert, NewCarAdvert, UsedCarAdvert}
import org.jooq.impl.DSL
import org.jooq.{DSLContext, Record, SQLDialect}
import play.api.db.Database
import repository.SortFieldType._
import repository._

import scala.collection.JavaConverters._

class JooqCarAdvertRepository @Inject()(db: Database) extends CarAdvertRepository {

  def withDSLContext[T](f: DSLContext => T): T = {
    db.withConnection { connection =>
      val dslContext = DSL.using(connection, SQLDialect.H2)
      f(dslContext)
    }
  }

  def toCarAdvert(record: Record): CarAdvert = {
    import CARADVERT._

    val isNew = record.get(ISNEW)
    if (isNew) {
      CarAdvert(
        record.get(ID),
        record.get(TITLE),
        record.get(FUEL),
        record.get(PRICE))
    } else {
      CarAdvert(
        record.get(ID),
        record.get(TITLE),
        record.get(FUEL),
        record.get(PRICE),
        record.get(MILEAGE),
        record.get(FIRSTREGISTRATION))
    }
  }

  override def get(): Seq[CarAdvert] = {
    withDSLContext(dslContext => {
      dslContext
        .select()
        .from(CARADVERT)
        .fetch()
        .asScala
        .map(toCarAdvert)
    })
  }

  def toTableField(sortField: SortField) = sortField match {
    case ID => CARADVERT.ID
    case TITLE => CARADVERT.TITLE
    case FUEL => CARADVERT.FUEL
    case PRICE => CARADVERT.PRICE
    case ISNEW => CARADVERT.ISNEW
    case MILEAGE => CARADVERT.MILEAGE
    case FIRSTREGISTRATION => CARADVERT.FIRSTREGISTRATION
  }

  override def get(sortField: SortField): Seq[CarAdvert] = {

    val tableField = toTableField(sortField)

    val adverts = DSL.select()
      .from(CARADVERT)
      .orderBy(tableField)

    withDSLContext(dslContext => {
      dslContext
        .select()
        .from(CARADVERT)
        .orderBy(tableField)
        .fetch()
        .asScala
        .map(toCarAdvert)
    })
  }

  override def getById(id: UUID): Option[CarAdvert] = {
    withDSLContext(dslContext => {
      Option(
        dslContext
          .select()
          .from(CARADVERT)
          .where(CARADVERT.ID.eq(id))
          .fetchOne())
        .map(toCarAdvert)
    })
  }

  def mileage(carAdvert: CarAdvert): Integer = {
    carAdvert match {
      case _: NewCarAdvert => null
      case usedCarAdvert: UsedCarAdvert => usedCarAdvert.mileage
    }
  }

  def firstRegistration(carAdvert: CarAdvert): LocalDate = {
    carAdvert match {
      case _: NewCarAdvert => null
      case usedCarAdvert: UsedCarAdvert => usedCarAdvert.firstRegistration
    }
  }

  override def add(carAdvert: CarAdvert): Boolean = {
    withDSLContext(dslContext => {
      import CARADVERT._

      val exists = dslContext
        .selectOne()
        .from(CARADVERT)
        .where(ID.equal(carAdvert.id))
        .execute() > 0

      val isNew = carAdvert.isInstanceOf[NewCarAdvert]
      if (!exists) {
        dslContext
          .insertInto(CARADVERT, ID, TITLE, FUEL, PRICE, ISNEW, MILEAGE, FIRSTREGISTRATION)
          .values(
            carAdvert.id,
            carAdvert.title,
            carAdvert.fuel,
            carAdvert.price,
            isNew,
            mileage(carAdvert),
            firstRegistration(carAdvert)
          )
          .execute()
      }
      !exists
    })
  }

  override def update(carAdvert: CarAdvert): Boolean = {
    withDSLContext(dslContext => {
      import CARADVERT._

      val updateStatement = dslContext
        .update(CARADVERT)
        .set(ID, carAdvert.id)
        .set(TITLE, carAdvert.title)
        .set(FUEL, carAdvert.fuel)
        .set(PRICE, int2Integer(carAdvert.price))

      carAdvert match {
        case newCarAdvert: NewCarAdvert =>
          updateStatement.set(ISNEW, boolean2Boolean(true))
        case usedCarAdvert: UsedCarAdvert =>
          updateStatement.set(ISNEW, boolean2Boolean(false))
            .set(MILEAGE, mileage(carAdvert))
            .set(FIRSTREGISTRATION, firstRegistration(carAdvert))
      }

      updateStatement.where(ID.equal(carAdvert.id)).execute() > 0
    })
  }

  override def delete(id: UUID): Boolean = {
    withDSLContext(dslContext => {
      import CARADVERT._

      dslContext
        .deleteFrom(CARADVERT)
        .where(ID.equal(id))
        .execute() > 0
    })
  }
}
