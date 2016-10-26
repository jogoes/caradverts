package controller

import java.time.LocalDate
import java.util.UUID

import controllers.CarAdvertController
import json.CarAdvertFormat._
import model.{CarAdvert, FuelType}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play._
import play.api.libs.json.Json
import play.api.mvc._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repository.{CarAdvertRepository, TransientInMemoryCarAdvertRepository}
import testutil.CarAdvertFactory

import scala.concurrent.Future

class CarAdvertControllerSpec extends PlaySpec with Results with MockitoSugar with BeforeAndAfterEach {

  var carAdvertRepository : CarAdvertRepository = _
  var controller : CarAdvertController = _

  val carAdverts = Seq(
    CarAdvertFactory.newCarAdvert("advert1", FuelType.GASOLINE),
    CarAdvertFactory.newCarAdvert("advert2", FuelType.DIESEL)
  )

  override protected def beforeEach(): Unit = {
    carAdvertRepository = new TransientInMemoryCarAdvertRepository
    carAdverts.foreach(carAdvertRepository.add)

    controller = new CarAdvertController(carAdvertRepository)
  }

  def toAdverts(result: Future[Result]) : Seq[CarAdvert] = contentAsJson(result).validate[Seq[CarAdvert]].get

  def toAdvert(result: Future[Result]) : Option[CarAdvert] = contentAsJson(result).validate[CarAdvert].asOpt

  implicit val localDateOrdering: Ordering[LocalDate] = Ordering.by(_.toEpochDay)

  "CarAdvertController" should {

    "return car adverts in repository" in {

      val result: Future[Result] = controller.carAdverts().apply(FakeRequest())

      status(result) must equal(OK)
      toAdverts(result) must contain theSameElementsAs carAdverts
    }

    "return existing car advert by id" in {
      val result: Future[Result] = controller.carAdvertById(carAdverts.head.id.toString).apply(FakeRequest())

      status(result) must equal(OK)
      toAdvert(result) must equal(Some(carAdverts.head))
    }

    "return not found error in case getting non-existing car advert by id" in {
      val result: Future[Result] = controller.carAdvertById(UUID.randomUUID().toString).apply(FakeRequest())

      status(result) must equal(NOT_FOUND)
    }

    "return error in case getting car advert by invalid id" in {
      val result: Future[Result] = controller.carAdvertById("some invalid id").apply(FakeRequest())

      status(result) must equal(BAD_REQUEST)
    }

    "return car adverts sorted by title" in {
      carAdvertRepository.add(CarAdvertFactory.newCarAdvert("aa", FuelType.GASOLINE))
      carAdvertRepository.add(CarAdvertFactory.newCarAdvert("zz", FuelType.GASOLINE))

      val result: Future[Result] = controller.carAdverts(Some("title")).apply(FakeRequest())

      status(result) must equal(OK)
      toAdverts(result).map(_.title) mustBe sorted
    }

    "return car adverts sorted by price" in {
      carAdvertRepository.add(CarAdvertFactory.newCarAdvert("aa", FuelType.GASOLINE))
      carAdvertRepository.add(CarAdvertFactory.newCarAdvert("zz", FuelType.GASOLINE))

      val result: Future[Result] = controller.carAdverts(Some("price")).apply(FakeRequest())
      status(result) must equal(OK)
      toAdverts(result).map(_.price) mustBe sorted
    }

    "return car adverts sorted by mileage" in {
      carAdvertRepository.add(CarAdvertFactory.usedCarAdvert("aa", FuelType.GASOLINE))
      carAdvertRepository.add(CarAdvertFactory.usedCarAdvert("zz", FuelType.GASOLINE))

      val result: Future[Result] = controller.carAdverts(Some("mileage")).apply(FakeRequest())
      status(result) must equal(OK)
      toAdverts(result).map(_.mileage) mustBe sorted
    }

    "return car adverts sorted by first registration" in {
      carAdvertRepository.add(CarAdvertFactory.usedCarAdvert("aa", FuelType.GASOLINE))
      carAdvertRepository.add(CarAdvertFactory.usedCarAdvert("zz", FuelType.GASOLINE))

      val result: Future[Result] = controller.carAdverts(Some("firstregistration")).apply(FakeRequest())
      status(result) must equal(OK)
      toAdverts(result).map(_.firstRegistration) mustBe sorted
    }

    "deleting existing advert should return ok" in {
      val result: Future[Result] = controller.deleteById(carAdverts.head.id.toString).apply(FakeRequest())

      status(result) must equal(OK)
    }

    "deleting non-existing advert should return error" in {
      val result: Future[Result] = controller.deleteById(UUID.randomUUID().toString).apply(FakeRequest())

      status(result) must equal(NOT_FOUND)
    }

    "adding advert with valid json should return ok" in {
      val json = Json.toJson(CarAdvertFactory.newCarAdvert("advert3", FuelType.GASOLINE))

      val result = controller.add().apply(FakeRequest().withBody(json))
      status(result) must equal(OK)
    }

    "adding advert twice with same id and valid json should return error" in {
      val json = Json.toJson(CarAdvertFactory.newCarAdvert("advert3", FuelType.GASOLINE))

      var result = controller.add().apply(FakeRequest().withBody(json))
      status(result) must equal(OK)
      result = controller.add().apply(FakeRequest().withBody(json))
      status(result) must equal(BAD_REQUEST)
    }

    "updating existing advert should return ok" in {
      val json = Json.toJson(carAdverts.head)

      val result = controller.update().apply(FakeRequest().withBody(json))
      status(result) must equal(OK)
    }

    "updating non-existing advert should return error" in {
      val json = Json.toJson(CarAdvertFactory.newCarAdvert("advert3", FuelType.GASOLINE))

      val result = controller.update().apply(FakeRequest().withBody(json))
      status(result) must equal(NOT_FOUND)
    }
  }
}
