package repository

import java.time.LocalDate
import java.util.UUID
import javax.inject._

import model.CarAdvert

/**
  * A simple repository implementation keeping all data in memory not persisting it to anywhere.
  *
  * This is mainly just for getting started having some implementation of a repositry or e.g. for mocking purposes.
  */
@Singleton
class TransientInMemoryCarAdvertRepository extends CarAdvertRepository {

  private var carAdverts = Map[UUID, CarAdvert]()

  override def get(): Seq[CarAdvert] = carAdverts.values.toSeq

  implicit val localDateOrdering: Ordering[LocalDate] = Ordering.by(_.toEpochDay)

  override def get(sortField: String): Seq[CarAdvert] = {
    val adverts = carAdverts.values.toSeq

    sortField.toLowerCase() match {
      case "title" => adverts.sortBy(_.title)
      case "fuel" => adverts.sortBy(_.fuel)
      case "price" => adverts.sortBy(_.price)
      case "isnew" => adverts.sortBy(_.isNew)
      case "mileage" => adverts.sortBy(_.mileage)
      case "firstregistration" => adverts.sortBy(_.firstRegistration)
      case _ => adverts.sortBy(_.id)
    }
  }

  override def getById(id: UUID): Option[CarAdvert] = carAdverts.get(id)

  /**
    * @return true if no advert with the same id existed and the new advert was added
    */
  override def add(carAdvert: CarAdvert): Boolean = {
    val exists = carAdverts.get(carAdvert.id).isDefined
    if(!exists) {
      carAdverts = carAdverts + (carAdvert.id -> carAdvert)
    }
    // return true if advert was added
    !exists
  }

  /**
    * @return true if an advert with the same id existed and the advert was updated
    */
  override def update(carAdvert: CarAdvert): Boolean = {
    val exists = carAdverts.get(carAdvert.id).isDefined
    if(exists) {
      carAdverts = carAdverts + (carAdvert.id -> carAdvert)
    }
    exists
  }

  override def delete(id: UUID): Boolean = {
    val advert = carAdverts.get(id)
    advert.foreach(_ => carAdverts = carAdverts - id)
    advert.isDefined
  }
}
