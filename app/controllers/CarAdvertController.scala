package controllers

import java.util.UUID
import javax.inject._

import json.CarAdvertFormat._
import model.CarAdvert
import play.api.data.validation.ValidationError
import play.api.libs.json.{JsError, JsObject, JsPath, Json}
import play.api.mvc._
import repository.{CarAdvertRepository, SortFields}

import scala.util.{Failure, Success, Try}

object ErrorCodes {
  val ITEM_ALREADY_EXISTING = 1000
  val ITEM_NOT_FOUND = 1001
  val INVALID_INPUT_DATA = 1002
}

object CarAdvertController {

  def toJson(errorCode: Int, message: String, errors: Seq[(JsPath, Seq[ValidationError])]): JsObject = {
    Json.obj(
      "code" -> errorCode,
      "errors" -> JsError.toJson(errors))
  }

  def toJson(errorCode: Int, message: String): JsObject = {
    Json.obj(
      "code" -> errorCode,
      "message" -> message)
  }
}

@Singleton
class CarAdvertController @Inject()(carAdvertRepository: CarAdvertRepository) extends Controller {

  import CarAdvertController._

  def withUuid(id: String, f: (UUID => Result)): Result = {
    Try(UUID.fromString(id)) match {
      case Success(uuid) => f(uuid)
      case Failure(ex) => BadRequest(toJson(ErrorCodes.INVALID_INPUT_DATA, s"Invalid UUID '$id': ${ex.getMessage}"))
    }
  }

  private def toBadRequest(errors: Seq[(JsPath, Seq[ValidationError])]): Result = {
    BadRequest(toJson(ErrorCodes.INVALID_INPUT_DATA, "Failed to parse provided JSON data.", errors))
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
      carAdvertRepository.getById(uuid) match {
        case Some(advert) => Ok(Json.toJson(advert))
        case _ => NotFound(toJson(ErrorCodes.ITEM_NOT_FOUND, s"Item with id '$id' not found."))
      }
    })
  }

  def deleteById(id: String) = Action {
    withUuid(id, uuid => {
      if (carAdvertRepository.delete(uuid)) {
        NoContent
      } else {
        NotFound(toJson(ErrorCodes.ITEM_NOT_FOUND, s"Item with id '$id' not found."))
      }
    })
  }

  def add = Action(BodyParsers.parse.json) { request =>
    request.body
      .validate[CarAdvert]
      .fold(
        errors => toBadRequest(errors),
        advert => {
          if (carAdvertRepository.add(advert)) {
            Created
          } else {
            BadRequest(toJson(ErrorCodes.ITEM_ALREADY_EXISTING, s"Item with id '${advert.id}' already exists."))
          }
        }
      )
  }

  def update = Action(BodyParsers.parse.json) { request =>
    request.body
      .validate[CarAdvert]
      .fold(
        errors => toBadRequest(errors),
        advert => {
          if (carAdvertRepository.update(advert)) {
            NoContent
          } else {
            NotFound(toJson(ErrorCodes.ITEM_NOT_FOUND, s"Item with id '$advert.id' not found."))
          }
        }
      )
  }
}
