package controller

import controllers.CarAdvertController
import json.CarAdvertFormat._
import model.{CarAdvert, FuelType}
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play._
import play.api.mvc._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repository.CarAdvertRepository
import testutil.CarAdvertFactory

import scala.concurrent.Future

class CarAdvertControllerSpec extends PlaySpec with Results with MockitoSugar with BeforeAndAfterEach {

  var carAdvertRepository : CarAdvertRepository = _

  val carAdverts = Seq(
    CarAdvertFactory.newCarAdvert("advert1", FuelType.GASOLINE),
    CarAdvertFactory.newCarAdvert("advert2", FuelType.DIESEL)
  )

  override protected def beforeEach(): Unit = {
    carAdvertRepository = mock[CarAdvertRepository]
  }

  "CarAdvertController" should {
    "return car adverts in repository" in {

      when(carAdvertRepository.get()) thenReturn carAdverts

      val controller = new CarAdvertController(carAdvertRepository)
      val result : Future[Result] = controller.carAdverts().apply(FakeRequest())

      status(result) must equal(OK)

      val adverts = contentAsJson(result).validate[Seq[CarAdvert]]
      adverts.get must contain theSameElementsAs carAdverts
    }
  }
}
