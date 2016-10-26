package controllers

import java.util.UUID
import javax.inject._

import json.CarAdvertFormat._
import model.CarAdvert
import play.api.data.validation.ValidationError
import play.api.libs.json.{JsError, JsPath, Json}
import play.api.mvc.{Action, BodyParsers, Controller, QueryStringBindable}
import repository.CarAdvertRepository


object CarAdvertController {
  def toJson(status: String, errors: Seq[(JsPath, Seq[ValidationError])]) = {
    Json.obj(
      "status" -> status,
      "message" -> JsError.toJson(errors))
  }

  def toJson(status: String, message: String) = {
    Json.obj(
      "status" -> status,
      "message" -> message)
  }

  def jsonSuccess(message: String) = toJson("OK", message)

  def jsonError(errors: Seq[(JsPath, Seq[ValidationError])]) = toJson("Error", errors)
}

@Singleton
class CarAdvertController @Inject()(carAdvertRepository: CarAdvertRepository) extends Controller {

  import CarAdvertController._

  implicit def uuidFromString(s: String) : UUID = UUID.fromString(s)

  def carAdverts() = Action {
    val json = Json.toJson(carAdvertRepository.get())
    Ok(json)
  }

  def carAdvertById(id: String) = Action {
    val json = Json.toJson(carAdvertRepository.getById(id))
    Ok(json)
  }

  def deleteById(id: String) = Action {
    carAdvertRepository.delete(id) match {
      case true => Ok(jsonSuccess(s"Item with id '$id' deleted from database."))
      case _ => NotFound
    }
  }

  def add = Action(BodyParsers.parse.json) { request =>
    val carAdvert = request.body.validate[CarAdvert]
    carAdvert.fold(
      errors => {
        BadRequest(jsonError(errors))
      },
      advert => {
        carAdvertRepository.add(advert)
        Ok(jsonSuccess(s"Item with id '${advert.id}' added to database."))
      }
    )
  }

  def update = Action(BodyParsers.parse.json) { request =>
    val carAdvert = request.body.validate[CarAdvert]
    carAdvert.fold(
      errors => {
        BadRequest(jsonError(errors))
      },
      advert => {
        carAdvertRepository.update(advert)
        Ok(jsonSuccess(s"Item with id '${advert.id}' added to database."))
      }
    )
  }
}
