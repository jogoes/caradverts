package repository

import java.util.UUID

import model.FuelType
import FuelType._
import org.scalatest.{BeforeAndAfterEach, FlatSpec, Matchers}
import testutil.CarAdvertFactory._

class TransientInMemoryCarAdvertRepositorySpec extends FlatSpec with Matchers with BeforeAndAfterEach {


  var repository: TransientInMemoryCarAdvertRepository = _

  val adverts = Seq(
    usedCarAdvert("advert1", GASOLINE),
    newCarAdvert("advert2", DIESEL)
  )

  override protected def beforeEach(): Unit = {
    repository = new TransientInMemoryCarAdvertRepository()
    fillRepository(repository)
  }

  private def fillRepository(repository: CarAdvertRepository) = adverts.foreach(repository.add)

  "TransientInMemoryCarAdvertRepository" should "add and get car adverts" in {
    val results = repository.get()
    repository.get() should contain theSameElementsAs adverts
  }

  it should "get car adverts by id" in {
    repository.getById(adverts.head.id) shouldBe Some(adverts.head)
  }

  it should "return none for not existing id" in {
    repository.getById(UUID.randomUUID()) shouldBe None
  }

  it should "update existing car advert" in {
    val newAdvert = newCarAdvert(adverts.head.id, "advert10", FuelType.GASOLINE)

    repository.update(newAdvert) shouldBe true
    repository.getById(adverts.head.id) shouldBe Some(newAdvert)
  }

  it should "ingore updating non-existing car advert" in {
    val newAdvert = usedCarAdvert("advert10", FuelType.GASOLINE)

    repository.update(newAdvert) shouldBe false
    repository.getById(adverts.head.id) shouldBe Some(adverts.head)
  }

  it should "delete existing car advert" in {
    repository.delete(adverts.head.id) shouldBe true
    repository.getById(adverts.head.id) shouldBe None
  }

  it should "not delete non-existing car advert" in {
    repository.delete(UUID.randomUUID) shouldBe false
    repository.get should contain theSameElementsAs adverts
  }
}
