package repository
import java.util.UUID

import model.CarAdvert

/**
  * A simple repository implementation keeping all data in memory not persisting it to anywhere.
  *
  * This is mainly just for getting started having some implementation of a repositry or e.g. for mocking purposes.
  */
class TransientInMemoryCarAdvertRepository extends CarAdvertRepository {

  private var carAdverts = Map[UUID, CarAdvert]()

  override def get(): Seq[CarAdvert] = carAdverts.values.toSeq

  override def getById(id: UUID): Option[CarAdvert] = carAdverts.get(id)

  override def add(carAdvert: CarAdvert): Unit = carAdverts = carAdverts + (carAdvert.id -> carAdvert)

  override def update(carAdvert: CarAdvert): Boolean = {
    val found = getById(carAdvert.id)
    found.foreach(_ => add(carAdvert)) // just add if existing since this implementation just replaces it when having the same id
    found.isDefined
  }

  override def delete(id: UUID): Boolean = {
    val advert = carAdverts.get(id)
    advert.foreach(_ => carAdverts = carAdverts - id)
    advert.isDefined
  }
}
