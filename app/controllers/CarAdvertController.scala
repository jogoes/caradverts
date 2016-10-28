package controllers

import java.util.UUID
import javax.inject._

import json.CarAdvertFormat._
import model.CarAdvert
import play.api.data.validation.ValidationError
import play.api.libs.json.{JsError, JsPath, Json}
import play.api.mvc._
import repository.{CarAdvertRepository, SortFields}


object ErrorCodes {
  val ITEM_ALREADY_EXISTING = 1000
  val ITEM_NOT_FOUND = 1001
  val INVALID_INPUT_DATA = 1002
}

object CarAdvertController {

  def toJson(errorCode: Int, message: String, errors: Seq[(JsPath, Seq[ValidationError])]) = {
    Json.obj(
      "code" -> errorCode,
      "errors" -> JsError.toJson(errors))
  }

  def toJson(errorCode: Int, message: String) = {
    Json.obj(
      "code" -> errorCode,
      "message" -> message)
  }
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
        BadRequest(toJson(ErrorCodes.INVALID_INPUT_DATA, s"Invalid UUID '${id}': ${e.getMessage}"))
    }
  }

  def index = Action {
    Ok("Car advert service running.")
  }

  def carAdverts(sortby: Option[String] = Some(SortFields.ID)) = Action {
    val json = Json.toJson(carAdvertRepository.get(sortby.getOrElse("id")))
    Ok(json)
  }

  def carAdvertById(id: String) = Action {
    withUuid(id, uuid => {
      carAdvertRepository.getById(uuid)
        .map(advert => Ok(Json.toJson(advert))
        ).getOrElse(NotFound(toJson(ErrorCodes.ITEM_NOT_FOUND, s"Item with id '$id' not found.")))
    })
  }

  def deleteById(id: String) = Action {
    withUuid(id, uuid => {
      carAdvertRepository.delete(uuid) match {
        case true => NoContent
        case _ => NotFound(toJson(ErrorCodes.ITEM_NOT_FOUND, s"Item with id '$id' not found."))
      }
    })
  }

  def add = Action(BodyParsers.parse.json) { request =>
    val carAdvert = request.body.validate[CarAdvert]
    carAdvert.fold(
      errors => {
        BadRequest(toJson(ErrorCodes.INVALID_INPUT_DATA, "Failed to parse provided JSON data.", errors))
      },
      advert => {
        carAdvertRepository.add(advert) match {
          case true => Created
          case _ => BadRequest(toJson(ErrorCodes.ITEM_ALREADY_EXISTING, s"Item with id '${advert.id}' already exists."))
        }
      }
    )
  }

  def update = Action(BodyParsers.parse.json) { request =>
    val carAdvert = request.body.validate[CarAdvert]
    carAdvert.fold(
      errors => {
        BadRequest(toJson(ErrorCodes.INVALID_INPUT_DATA, "Failed to parse provided JSON data.", errors))
      },
      advert => {
        carAdvertRepository.update(advert) match {
          case true => NoContent
          case _ => NotFound(toJson(ErrorCodes.ITEM_NOT_FOUND, s"Item with id '$advert.id' not found."))
        }
      }
    )
  }
}
