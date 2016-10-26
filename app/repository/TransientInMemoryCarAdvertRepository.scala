package repository

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

  override def getById(id: UUID): Option[CarAdvert] = carAdverts.get(id)

  override def add(carAdvert: CarAdvert): Boolean = {
    val exists = carAdverts.get(carAdvert.id).isDefined
    if(!exists) {
      carAdverts = carAdverts + (carAdvert.id -> carAdvert)
    }
    // return true if advert was added
    !exists
  }

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
