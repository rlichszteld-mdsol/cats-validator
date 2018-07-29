package com.rlich.json.parsing
import java.util.UUID

import com.rlich.json.parsing.CommonErrors.{FieldTypeError, MissingFieldError}
import com.rlich.json.parsing.JsonParsing.{JsConverter, OptionalField, Parsed, ParsingError}
import com.rlich.json.utils.UUIDParser
import spray.json.{JsNumber, JsObject, JsString, JsValue}

object CommonErrors {
  case class MissingFieldError(fieldName: String) extends ParsingError

  case class FieldTypeError(fieldName: String, value: JsValue) extends ParsingError
}

trait CommonJsValueConverters {

  def stringConverter: JsConverter[String] = {
    case string: JsString => string.value
  }

  def intConverter: JsConverter[Int] = {
    case number: JsNumber => number.value.toInt
  }

  def uuidConverter: JsConverter[UUID] = {
    case string: JsString => UUIDParser.parseString(string.value)
  }
}

trait DefaultJsonParseSupport extends JsonParseSupport with CommonJsValueConverters {
  def defaultMissingFieldHandler: String => MissingFieldError = fieldName => MissingFieldError(fieldName)

  def defaultParseErrorHandler: (String, JsValue) => FieldTypeError =
    (fieldName: String, value: JsValue) => FieldTypeError(fieldName, value)

  def readString(obj: JsObject, fieldName: String): Parsed[String] = {
    readField(obj, fieldName, stringConverter, defaultParseErrorHandler, defaultMissingFieldHandler)
  }

  def readInt(obj: JsObject, fieldName: String): Parsed[Int] = {
    readField(obj, fieldName, intConverter, defaultParseErrorHandler, defaultMissingFieldHandler)
  }

  def readUuid(obj: JsObject, fieldName: String): Parsed[UUID] = {
    readField(obj, fieldName, uuidConverter, defaultParseErrorHandler, defaultMissingFieldHandler)
  }

  def readStringOptional(obj: JsObject, fieldName: String): Parsed[OptionalField[String]] = {
    readFieldOptional(obj, fieldName, stringConverter, defaultParseErrorHandler)
  }

  def readIntOptional(obj: JsObject, fieldName: String): Parsed[OptionalField[Int]] = {
    readFieldOptional(obj, fieldName, intConverter, defaultParseErrorHandler)
  }

  def readUuidOptional(obj: JsObject, fieldName: String): Parsed[OptionalField[UUID]] = {
    readFieldOptional(obj, fieldName, uuidConverter, defaultParseErrorHandler)
  }
}
