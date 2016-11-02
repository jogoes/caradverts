package repository.jooq

import java.util.UUID
import javax.inject.Inject

import generated.jooq.tables.Caradvert._
import model.CarAdvert
import org.jooq.impl.DSL
import org.jooq.{DSLContext, Record, SQLDialect}
import play.api.db.Database
import repository.{CarAdvertRepository, SortFields}

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

    val mileage: Option[Int] = record.get(MILEAGE) match {
      case null => None
      case m => Some(m)
    }

    CarAdvert(
      record.get(ID),
      record.get(TITLE),
      record.get(FUEL),
      record.get(PRICE),
      record.get(ISNEW),
      mileage,
      Option(record.get(FIRSTREGISTRATION)))

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

  private val sortFieldMap = Map(
    SortFields.ID -> CARADVERT.ID,
    SortFields.TITLE -> CARADVERT.TITLE,
    SortFields.FUEL -> CARADVERT.FUEL,
    SortFields.PRICE -> CARADVERT.PRICE,
    SortFields.ISNEW -> CARADVERT.ISNEW,
    SortFields.MILEAGE -> CARADVERT.MILEAGE,
    SortFields.FIRSTREGISTRATION -> CARADVERT.FIRSTREGISTRATION
  )

  override def get(sortField: String): Seq[CarAdvert] = {

    val tableField = sortFieldMap.getOrElse(sortField, CARADVERT.ID)

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
    carAdvert.mileage match {
      case Some(m) => m
      case None => null
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

      if(!exists) {
        dslContext
          .insertInto(CARADVERT, ID, TITLE, FUEL, PRICE, ISNEW, MILEAGE, FIRSTREGISTRATION)
          .values(
            carAdvert.id,
            carAdvert.title,
            carAdvert.fuel,
            carAdvert.price,
            carAdvert.isNew,
            mileage(carAdvert),
            carAdvert.firstRegistration.orNull
          )
          .execute()
      }
      !exists
    })
  }

  override def update(carAdvert: CarAdvert): Boolean = {
    withDSLContext(dslContext => {
      import CARADVERT._

      dslContext
        .update(CARADVERT)
        .set(ID, carAdvert.id)
        .set(TITLE, carAdvert.title)
        .set(FUEL, carAdvert.fuel)
        .set(PRICE, int2Integer(carAdvert.price))
        .set(ISNEW, boolean2Boolean(carAdvert.isNew))
        .set(MILEAGE, mileage(carAdvert))
        .set(FIRSTREGISTRATION, carAdvert.firstRegistration.orNull)
        .where(ID.equal(carAdvert.id))
        .execute() > 0
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
