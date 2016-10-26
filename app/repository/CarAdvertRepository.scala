package repository

import java.util.UUID

import model.CarAdvert

trait CarAdvertRepository {

  def get(): Seq[CarAdvert]

  def get(sortField: String): Seq[CarAdvert]

  def getById(id: UUID): Option[CarAdvert]

  def add(carAdvert: CarAdvert): Boolean

  def update(carAdvert: CarAdvert): Boolean

  def delete(id: UUID): Boolean

}
