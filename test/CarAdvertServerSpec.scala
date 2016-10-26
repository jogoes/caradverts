import json.CarAdvertFormat._
import model.{CarAdvert, FuelType}
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.play._
import play.api.http.Status._
import play.api.libs.json.Json
import play.api.libs.ws.{WSClient, WSRequest}
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}
import testutil.CarAdvertFactory

class CarAdvertServerSpec extends PlaySpec with OneServerPerSuite with DefaultAwaitTimeout with FutureAwaits with BeforeAndAfterEach {

  var address : String = _
  var url : String = _
  var wsClient : WSClient = _
  var request : WSRequest = _

  override protected def beforeEach(): Unit = {
    address = s"localhost:$port"
    url = s"http://$address/caradverts"
    wsClient = app.injector.instanceOf[WSClient]
    request = wsClient.url(url)
  }

  "test adding car adverts" in {
    val advert1 = CarAdvertFactory.newCarAdvert("advert1", FuelType.GASOLINE)
    val advert2 = CarAdvertFactory.newCarAdvert("advert2", FuelType.DIESEL)

    {
      var response = await(request.post(Json.toJson(advert1)))
      response.status mustBe OK
      // adding twice should return error
      response = await(request.post(Json.toJson(advert1)))
      response.status mustBe BAD_REQUEST
      response = await(request.post(Json.toJson(advert2)))
      response.status mustBe OK
    }

    // verify the adverts have been added
    {
      var response = await(request.get())
      response.status mustBe OK
      val carAdverts = Json.parse(response.body).validate[Seq[CarAdvert]]
      carAdverts.get must contain theSameElementsAs Seq(advert1, advert2)
    }

    // update car advert
    val advert3 = CarAdvertFactory.newCarAdvert(advert2.id, "advert3", FuelType.GASOLINE)

    {
      val response = await(request.withHeaders(("Content-Type", "application/json")).put(Json.toJson(advert3)))
      println(response.body)
      response.status mustBe OK
    }

    // verify the adverts have been updated
    {
      var response = await(request.get())
      response.status mustBe OK
      val carAdverts = Json.parse(response.body).validate[Seq[CarAdvert]]
      carAdverts.get must contain theSameElementsAs Seq(advert1, advert3)
    }

    // delete car advert
    {
      val response = await(wsClient.url(s"$url/${advert1.id}").delete())
      response.status mustBe OK
    }

    // verify item was deleted
    {
      val response = await(request.get())
      response.status mustBe OK
      val carAdverts = Json.parse(response.body).validate[Seq[CarAdvert]]
      carAdverts.get must contain theSameElementsAs Seq(advert3)
    }

  }
}
