package com.rlich.json.test

import cats.data.NonEmptyList
import cats.data.Validated.{Invalid, Valid}
import com.rlich.json.core.{Parsed, ParsingError}
import spray.json._
import cats.implicits._
import com.rlich.json.test.StudyEnvironmentCountryStatus.StudyEnvironmentCountryStatus

import scala.language.implicitConversions
import scala.util.{Success, Try}

class EnumJsonProtocol[T <: Enumeration](enum: T) extends RootJsonFormat[T#Value] {

  def read(json: JsValue): T#Value = json match {
    case JsString(x) =>
      enum.values
        .find(_.toString === x)
        .getOrElse(throw DeserializationException(s"$x is not a valid value from enum $enum"))
    case somethingElse =>
      throw DeserializationException(s"Expected a value from enum $enum instead of $somethingElse")
  }

  def write(obj: T#Value): JsValue = JsString(obj.toString)
}

object StudyEnvironmentCountryStatus extends Enumeration {
  type StudyEnvironmentCountryStatus = Value
  val planned, open, proposed, preparation, execution, completed, closed, not_selected, hold, stopped, canceled =
    Value

  implicit def toString(value: Value): String = value.toString
}

trait StudyEnvironmentCountryStatusJsonProtocol {
  implicit lazy val studyEnvironmentCountryStatusFormat: EnumJsonProtocol[StudyEnvironmentCountryStatus.type] =
    new EnumJsonProtocol(
      StudyEnvironmentCountryStatus
    )
}

object StudyEnvironmentCountryStatusJsonProtocol extends StudyEnvironmentCountryStatusJsonProtocol

case object SerializationError extends ParsingError

trait ParsedFormats {
  class ParsedFormat[T: JsonFormat] extends JsonFormat[Parsed[T]] {
    def write(obj: Parsed[T]): JsValue = obj match {
      case Valid(x) => x.toJson
      case _ => JsNull
    }

    def read(json: JsValue): Parsed[T] = {
      Try(json.convertTo[T]) match {
        case Success(x) => x.validNel
        case _ => SerializationError.invalidNel
      }
    }
  }

  implicit def parsedFormat[T: JsonFormat]: ParsedFormat[T] = new ParsedFormat[T]
}

case class StatusModel(status: StudyEnvironmentCountryStatus)

trait StatusModelJsonProtocol extends DefaultJsonProtocol {
  import StudyEnvironmentCountryStatusJsonProtocol._

  implicit lazy val statusModelFormat: RootJsonFormat[StatusModel] = jsonFormat1(StatusModel)
}

object Main extends App with ParsedFormats with StatusModelJsonProtocol {
  def processResult[T](result: Parsed[T]): Unit = {
    result match {
      case Valid(data) => println(s"Data: $data")
      case Invalid(nel: NonEmptyList[_]) =>
        println("Validation errors:")
        nel.toList.foreach(error => println(s" - $error"))
    }
  }

  val json =
    """
      | {
      |   "status": "plannedX"
      | }
    """.stripMargin

  val obj = json.parseJson.convertTo[Parsed[StatusModel]]
  processResult(obj)
}
