package controllers

import java.util.UUID
import javax.inject._

import json.CarAdvertFormat._
import model.CarAdvert
import play.api.data.validation.ValidationError
import play.api.libs.json.{JsError, JsPath, Json}
import play.api.mvc._
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

  def uuidFromString(s: String): UUID = UUID.fromString(s)

  def withUuid(id: String, f: (UUID => Result)): Result = {
    try {
      f(uuidFromString(id))
    } catch {
      case e: IllegalArgumentException =>
        BadRequest(s"Invalid UUID: ${e.getMessage}")
    }
  }

  def carAdverts() = Action {
    val json = Json.toJson(carAdvertRepository.get())
    Ok(json)
  }

  def carAdvertById(id: String) = Action {
    withUuid(id, uuid => {
      carAdvertRepository.getById(uuid)
        .map(advert => Ok(Json.toJson(advert))
        ).getOrElse(NotFound)
    })
  }

  def deleteById(id: String) = Action {
    withUuid(id, uuid => {
      carAdvertRepository.delete(uuid) match {
        case true => Ok(jsonSuccess(s"Item with id '$id' deleted from database."))
        case _ => NotFound
      }
    })
  }

  def add = Action(BodyParsers.parse.json) { request =>
    val carAdvert = request.body.validate[CarAdvert]
    carAdvert.fold(
      errors => {
        BadRequest(jsonError(errors))
      },
      advert => {
        carAdvertRepository.add(advert) match {
          case true => Ok(jsonSuccess(s"Item with id '${advert.id}' added to database."))
          case _ => BadRequest(s"Item with id '${advert.id}' already exists.")
        }
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
        carAdvertRepository.update(advert) match {
          case true => Ok(jsonSuccess(s"Item with id '${advert.id}' has been updated."))
          case _ => NotFound(s"Item with id '${advert.id}' not found.")
        }
      }
    )
  }
}
