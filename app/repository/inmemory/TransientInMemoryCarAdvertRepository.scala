package repository.inmemory

import java.util.UUID
import javax.inject._

import model.CarAdvert
import repository.Orderings._
import repository._

/**
  * A simple repository implementation keeping all data in memory not persisting it to anywhere.
  *
  * This is mainly just for getting started having some implementation of a repositry or e.g. for mocking purposes.
  */
@Singleton
class TransientInMemoryCarAdvertRepository extends CarAdvertRepository {

  private var carAdverts = Map[UUID, CarAdvert]()

  override def get(): Seq[CarAdvert] = carAdverts.values.toSeq

  override def get(sortField: SortField): Seq[CarAdvert] = {
    val adverts = carAdverts.values.toSeq

    import SortFieldType._

    sortField match {
      case TITLE => adverts.sortBy(_.title)
      case FUEL => adverts.sortBy(_.fuel)
      case PRICE => adverts.sortBy(_.price)
      case ISNEW => adverts.sortBy(_.isNew)
      case MILEAGE => adverts.sortBy(_.mileage)
      case FIRSTREGISTRATION => adverts.sortBy(_.firstRegistration)
      case ID => adverts.sortBy(_.id)
    }
  }

  override def getById(id: UUID): Option[CarAdvert] = carAdverts.get(id)

  /**
    * @return true if no advert with the same id existed and the new advert was added
    */
  override def add(carAdvert: CarAdvert): Boolean = {
    val exists = carAdverts.get(carAdvert.id).isDefined
    if (!exists) {
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
    if (exists) {
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
