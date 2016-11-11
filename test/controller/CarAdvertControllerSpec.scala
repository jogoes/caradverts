package controller

import java.util.UUID

import controllers.{CarAdvertController, ErrorCodes}
import json.CarAdvertFormat._
import model.CarAdvert
import model.FuelTypes._
import org.scalatest.BeforeAndAfterEach
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play._
import play.api.libs.json.Json
import play.api.mvc._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repository.Orderings._
import repository.SortFieldType._
import repository._
import repository.inmemory.TransientInMemoryCarAdvertRepository
import testutil.CarAdvertFactory

import scala.concurrent.Future

class CarAdvertControllerSpec extends PlaySpec with Results with MockitoSugar with BeforeAndAfterEach {

  var carAdvertRepository : CarAdvertRepository = _
  var controller : CarAdvertController = _

  val carAdverts = Seq(
    CarAdvertFactory.newCarAdvert("advert1", GASOLINE),
    CarAdvertFactory.newCarAdvert("advert2", DIESEL)
  )

  override protected def beforeEach(): Unit = {
    carAdvertRepository = new TransientInMemoryCarAdvertRepository
    carAdverts.foreach(carAdvertRepository.add)

    controller = new CarAdvertController(carAdvertRepository)
  }

  def toAdverts(result: Future[Result]) : Seq[CarAdvert] = contentAsJson(result).validate[Seq[CarAdvert]].get

  def toAdvert(result: Future[Result]) : Option[CarAdvert] = contentAsJson(result).validate[CarAdvert].asOpt

  def validateCodeAndMessage(result: Future[Result], code: Int): Unit = {
    val jsonError = contentAsJson(result)
    (jsonError\ "code").get.validate[Int].get mustBe code
    (jsonError \ "message").get.validate[String].get mustNot be(empty)
  }

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
      validateCodeAndMessage(result, ErrorCodes.ITEM_NOT_FOUND)
    }

    "return error in case getting car advert by invalid id" in {
      val result: Future[Result] = controller.carAdvertById("some invalid id").apply(FakeRequest())

      status(result) must equal(BAD_REQUEST)
      validateCodeAndMessage(result, ErrorCodes.INVALID_INPUT_DATA)
    }

    "return car adverts sorted by title" in {
      carAdvertRepository.add(CarAdvertFactory.newCarAdvert("aa", GASOLINE))
      carAdvertRepository.add(CarAdvertFactory.newCarAdvert("zz", GASOLINE))

      val result: Future[Result] = controller.carAdverts(Some(TITLE.name)).apply(FakeRequest())

      status(result) must equal(OK)
      toAdverts(result).map(_.title) mustBe sorted
    }

    "return car adverts sorted by price" in {
      carAdvertRepository.add(CarAdvertFactory.newCarAdvert("aa", GASOLINE))
      carAdvertRepository.add(CarAdvertFactory.newCarAdvert("zz", GASOLINE))

      val result: Future[Result] = controller.carAdverts(Some(PRICE.name)).apply(FakeRequest())
      status(result) must equal(OK)
      toAdverts(result).map(_.price) mustBe sorted
    }

    "return car adverts sorted by mileage" in {
      carAdvertRepository.add(CarAdvertFactory.usedCarAdvert("aa", GASOLINE))
      carAdvertRepository.add(CarAdvertFactory.usedCarAdvert("zz", GASOLINE))

      val result: Future[Result] = controller.carAdverts(Some(MILEAGE.name)).apply(FakeRequest())
      status(result) must equal(OK)
      toAdverts(result).map(_.mileage) mustBe sorted
    }

    "return car adverts sorted by first registration" in {
      carAdvertRepository.add(CarAdvertFactory.usedCarAdvert("aa", GASOLINE))
      carAdvertRepository.add(CarAdvertFactory.usedCarAdvert("zz", GASOLINE))

      val result: Future[Result] = controller.carAdverts(Some(FIRSTREGISTRATION.name)).apply(FakeRequest())
      status(result) must equal(OK)
      toAdverts(result).map(_.firstRegistration) mustBe sorted
    }

    "deleting existing advert should return ok" in {
      val result: Future[Result] = controller.deleteById(carAdverts.head.id.toString).apply(FakeRequest())

      status(result) must equal(NO_CONTENT)
    }

    "deleting non-existing advert should return error" in {
      val result: Future[Result] = controller.deleteById(UUID.randomUUID().toString).apply(FakeRequest())

      status(result) must equal(NOT_FOUND)
      validateCodeAndMessage(result, 1001)
    }

    "adding advert with valid json should return ok" in {
      val json = Json.toJson(CarAdvertFactory.newCarAdvert("advert3", GASOLINE))

      val result = controller.add().apply(FakeRequest().withBody(json))
      status(result) must equal(CREATED)
    }

    "adding advert twice with same id and valid json should return error" in {
      val json = Json.toJson(CarAdvertFactory.newCarAdvert("advert3", GASOLINE))

      var result = controller.add().apply(FakeRequest().withBody(json))
      status(result) must equal(CREATED)
      result = controller.add().apply(FakeRequest().withBody(json))
      status(result) must equal(BAD_REQUEST)
      validateCodeAndMessage(result, ErrorCodes.ITEM_ALREADY_EXISTING)
    }

    "updating existing advert should return ok" in {
      val json = Json.toJson(carAdverts.head)

      val result = controller.update().apply(FakeRequest().withBody(json))
      status(result) must equal(NO_CONTENT)
    }

    "updating non-existing advert should return error" in {
      val json = Json.toJson(CarAdvertFactory.newCarAdvert("advert3", GASOLINE))

      val result = controller.update().apply(FakeRequest().withBody(json))
      status(result) must equal(NOT_FOUND)
      validateCodeAndMessage(result, ErrorCodes.ITEM_NOT_FOUND)
    }
  }
}
