import json.CarAdvertFormat._
import model.{CarAdvert, FuelType}
import org.scalatestplus.play._
import play.api.http.Status._
import play.api.libs.json.Json
import play.api.libs.ws.{WSClient, WSRequest, WSResponse}
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}
import testutil.CarAdvertFactory

class CarAdvertServerSpec extends PlaySpec with OneServerPerTest with DefaultAwaitTimeout with FutureAwaits {

  def address: String = s"localhost:$port"

  def url: String = s"http://$address/caradverts"

  def wsClient: WSClient = app.injector.instanceOf[WSClient]

  def request: WSRequest = wsClient.url(url)

  def toAdverts(response: WSResponse): Seq[CarAdvert] = Json.parse(response.body).validate[Seq[CarAdvert]].get

  "test adding car adverts" in {
    val advert1 = CarAdvertFactory.newCarAdvert("advert1", FuelType.GASOLINE)
    val advert2 = CarAdvertFactory.newCarAdvert("advert2", FuelType.DIESEL)

    {
      var response = await(request.post(Json.toJson(advert1)))
      response.status mustBe CREATED
      // adding twice should return error
      response = await(request.post(Json.toJson(advert1)))
      response.status mustBe BAD_REQUEST
      response = await(request.post(Json.toJson(advert2)))
      response.status mustBe CREATED
    }

    // verify the adverts have been added
    {
      var response = await(request.get())
      response.status mustBe OK
      val carAdverts = toAdverts(response)
      carAdverts must contain theSameElementsAs Seq(advert1, advert2)
    }

    // update car advert
    val advert3 = CarAdvertFactory.newCarAdvert(advert2.id, "advert3", FuelType.GASOLINE)

    {
      val response = await(request.withHeaders(("Content-Type", "application/json")).put(Json.toJson(advert3)))
      response.status mustBe NO_CONTENT
    }

    // verify the adverts have been updated
    {
      var response = await(request.get())
      response.status mustBe OK
      val carAdverts = toAdverts(response)
      carAdverts must contain theSameElementsAs Seq(advert1, advert3)
    }

    // delete car advert
    {
      val response = await(wsClient.url(s"$url/${advert1.id}").delete())
      response.status mustBe NO_CONTENT
    }

    // verify item was deleted
    {
      val response = await(request.get())
      response.status mustBe OK
      val carAdverts = toAdverts(response)
      carAdverts must contain theSameElementsAs Seq(advert3)
    }
  }

  "car adverts are returned sorted" in {
    val adverts = Seq(
      CarAdvertFactory.newCarAdvert("advert1", FuelType.GASOLINE),
      CarAdvertFactory.newCarAdvert("advert2", FuelType.DIESEL)
    )

    adverts.foreach(advert => {
      var response = await(request.post(Json.toJson(advert)))
      response.status mustBe CREATED
    })

    {
      val response = await(request.withQueryString("sortby" -> "title").get())
      response.status mustBe OK
      val carAdverts = toAdverts(response)
      carAdverts.length mustBe adverts.length
      carAdverts.map(_.title) mustBe sorted
    }

    {
      val response = await(request.withQueryString("sortby" -> "mileage").get())
      response.status mustBe OK
      val carAdverts = toAdverts(response)
      carAdverts.length mustBe adverts.length
      carAdverts.map(_.mileage) mustBe sorted
    }
  }
}
