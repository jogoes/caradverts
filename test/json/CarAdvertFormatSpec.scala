package json

import java.time.LocalDate
import java.util.UUID

import json.CarAdvertFormat._
import model.{CarAdvert, FuelType}
import org.scalatest.{FlatSpec, Matchers}
import play.api.libs.json.{JsSuccess, Json}

class CarAdvertFormatSpec extends FlatSpec with Matchers {

  val uuid = UUID.fromString("75fb4ade-f5c7-4da3-b88a-4d6b7d8c42a8")
  val advertUsed = CarAdvert(uuid, "advert1", FuelType.GASOLINE, 1234, isNew = false, Some(5678), Some(LocalDate.of(2016, 10, 11)))
  val jsonAdvertUsed = """{"id":"75fb4ade-f5c7-4da3-b88a-4d6b7d8c42a8","title":"advert1","fuel":"GASOLINE","price":1234,"isnew":false,"mileage":5678,"firstRegistration":"2016-10-11"}"""
  val advertNew = CarAdvert(uuid, "advert1", FuelType.GASOLINE, 1234, isNew = true, None, None)
  val jsonAdvertNew = """{"id":"75fb4ade-f5c7-4da3-b88a-4d6b7d8c42a8","title":"advert1","fuel":"GASOLINE","price":1234,"isnew":true}"""

  "CarAdvertFormat" should "convert object to json" in {
    Json.toJson(advertUsed).toString should equal(jsonAdvertUsed)
  }

  it should "omit none members from json" in {
    val uuid = UUID.fromString("75fb4ade-f5c7-4da3-b88a-4d6b7d8c42a8")
    Json.toJson(advertNew).toString should equal(jsonAdvertNew)
  }

  it should "parse used car advert with all properties set" in {
    val advert = Json.parse(jsonAdvertUsed).validate[CarAdvert]
    advert should equal (JsSuccess(advertUsed))
  }

  it should "parse car advert json with not existing properties" in {
    val advert = Json.parse(jsonAdvertNew).validate[CarAdvert]
    advert should equal (JsSuccess(advertNew))
  }
}
