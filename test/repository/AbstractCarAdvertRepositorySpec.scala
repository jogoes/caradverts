package repository

import java.util.UUID

import model.FuelTypes._
import org.scalatest.{BeforeAndAfterEach, FlatSpec, Matchers}
import repository.Orderings._
import repository.SortFieldType._
import testutil.CarAdvertFactory._
import testutil.CarAdvertHelpers._

abstract class AbstractCarAdvertRepositorySpec extends FlatSpec with Matchers with BeforeAndAfterEach {

  def repositoryFactory: () => CarAdvertRepository

  var repository: CarAdvertRepository = _

  val adverts = Seq(
    usedCarAdvert("advert1", GASOLINE),
    newCarAdvert("advert3", DIESEL),
    usedCarAdvert("advert2", DIESEL),
    newCarAdvert("advert4", GASOLINE)
  )

  override protected def beforeEach(): Unit = {
    repository = repositoryFactory()
    fillRepository(repository)
  }

  private def fillRepository(repository: CarAdvertRepository) = adverts.foreach(repository.add)

  "CarAdvertRepository" should "add and get car adverts" in {
    val results = repository.get()
    repository.get() should contain theSameElementsAs adverts
  }

  it should "not insert item with same id twice" in {
    repository.add(adverts.head) shouldBe false
  }

  it should "return car adverts sorted by id" in {
    repository.get(ID).map(_.id) shouldBe sorted
  }

  it should "return car adverts sorted by title" in {
    repository.get(TITLE).map(_.title) shouldBe sorted
  }

  it should "return car adverts sorted by fuel" in {
    repository.get(FUEL).map(_.fuel) shouldBe sorted
  }

  it should "return car adverts sorted by price" in {
    repository.get(PRICE).map(_.price) shouldBe sorted
  }

  it should "return car adverts sorted by mileage" in {
    repository.get(MILEAGE).map(toMileage) shouldBe sorted
  }

  it should "return car adverts sorted by firstregistration" in {
    repository.get(FIRSTREGISTRATION).map(toFirstRegistration) shouldBe sorted
  }

  it should "get car adverts by id" in {
    repository.getById(adverts.head.id) shouldBe adverts.headOption
  }

  it should "return none for not existing id" in {
    repository.getById(UUID.randomUUID()) shouldBe None
  }

  it should "update existing car advert" in {
    val newAdvert = newCarAdvert(adverts.head.id, "advert10", GASOLINE)

    repository.update(newAdvert) shouldBe true
    val advert = repository.getById(adverts.head.id)
    advert shouldBe Some(newAdvert)
  }

  it should "ignore updating non-existing car advert" in {
    val newAdvert = usedCarAdvert("advert10", GASOLINE)

    repository.update(newAdvert) shouldBe false
    repository.get() should contain theSameElementsAs adverts
  }

  it should "delete existing car advert" in {
    repository.delete(adverts.head.id) shouldBe true
    repository.getById(adverts.head.id) shouldBe None
  }

  it should "delete existing car advert and add it again" in {
    repository.delete(adverts.head.id) shouldBe true
    repository.getById(adverts.head.id) shouldBe None
    repository.add(adverts.head) shouldBe true
    repository.getById(adverts.head.id) shouldBe adverts.headOption
  }

  it should "not delete non-existing car advert" in {
    repository.delete(UUID.randomUUID) shouldBe false
    repository.get should contain theSameElementsAs adverts
  }
}

