import json.CarAdvertFormat._
import model.{CarAdvert, FuelType}
import org.scalatestplus.play._
import play.api.http.Status._
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}
import testutil.CarAdvertFactory

class CarAdvertServerSpec extends PlaySpec with OneServerPerSuite with DefaultAwaitTimeout with FutureAwaits {

  "test adding car adverts" in {

    val advert = CarAdvertFactory.newCarAdvert("advert1", FuelType.GASOLINE)

    val wsClient = app.injector.instanceOf[WSClient]
    val address = s"localhost:$port"
    val url = s"http://$address/caradverts"

    var addResponse = await(wsClient.url(url).post(Json.toJson(advert)))
    addResponse.status mustBe OK
    addResponse = await(wsClient.url(url).post(Json.toJson(advert)))
    addResponse.status mustBe OK

    val response = await(wsClient.url(url).get())

    response.status mustBe OK
    val carAdverts = Json.parse(response.body).validate[Seq[CarAdvert]]
    carAdverts.get must contain theSameElementsAs Seq(advert)
  }
}
