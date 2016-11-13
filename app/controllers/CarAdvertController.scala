package controllers

import java.util.UUID
import javax.inject._

import json.CarAdvertFormat._
import model.CarAdvert
import play.api.data.validation.ValidationError
import play.api.libs.json.{JsError, JsObject, JsPath, Json}
import play.api.mvc._
import repository.SortFieldType._
import repository.{CarAdvertRepository, SortFieldType}

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

  def badRequest(errorCode: Int, message: String): Result = BadRequest(toJson(errorCode, message))

  def badRequest(errorCode: Int, message: String, errors: Seq[(JsPath, Seq[ValidationError])]): Result = BadRequest(toJson(errorCode, message, errors))

  def badRequest(errors: Seq[(JsPath, Seq[ValidationError])]): Result = badRequest(ErrorCodes.INVALID_INPUT_DATA, "Failed to parse provided JSON data.", errors)

  def notFound(id: String): Result = NotFound(toJson(ErrorCodes.ITEM_NOT_FOUND, s"Item with id '$id' not found."))

  def withUuid(id: String, f: (UUID => Result)): Result = {
    Try(UUID.fromString(id)) match {
      case Success(uuid) => f(uuid)
      case Failure(ex) => badRequest(ErrorCodes.INVALID_INPUT_DATA, s"Invalid UUID '$id': ${ex.getMessage}")
    }
  }

  def index = Action {
    Ok("Car advert service running.")
  }

  def carAdverts(sortby: Option[String] = Some(ID.name)) = Action {
    val sortField = sortby
      .flatMap(SortFieldType.fromString)
      .getOrElse(ID)
    val json = Json.toJson(carAdvertRepository.get(sortField))
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
        errors => badRequest(errors),
        advert => {
          if (carAdvertRepository.add(advert)) {
            Created
          } else {
            badRequest(ErrorCodes.ITEM_ALREADY_EXISTING, s"Item with id '${advert.id}' already exists.")
          }
        }
      )
  }

  def update = Action(BodyParsers.parse.json) { request =>
    request.body
      .validate[CarAdvert]
      .fold(
        errors => badRequest(errors),
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
