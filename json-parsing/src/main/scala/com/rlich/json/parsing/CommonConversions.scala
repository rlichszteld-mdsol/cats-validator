package com.rlich.json.parsing
import java.util.UUID

import com.rlich.json.parsing.CommonErrors.{FieldTypeError, MissingFieldError}
import com.rlich.json.parsing.JsonParsing._
import com.rlich.json.utils.UUIDParser
import spray.json.{JsBoolean, JsNumber, JsObject, JsString, JsValue}

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

  def booleanConverter: JsConverter[Boolean] = {
    case bool: JsBoolean => bool.value
  }

  def uuidConverter: JsConverter[UUID] = {
    case string: JsString => UUIDParser.parseString(string.value)
  }
}

trait DefaultJsonParseSupport extends JsonParseSupport with CommonJsValueConverters {
  def defaultMissingFieldHandler: String => MissingFieldError = fieldName => MissingFieldError(fieldName)

  def defaultParseErrorHandler: (String, JsValue) => FieldTypeError =
    (fieldName: String, value: JsValue) => FieldTypeError(fieldName, value)

  def readString(obj: JsObject,
                 fieldName: String,
                 onParseError: ParseErrorHandler = defaultParseErrorHandler,
                 onFieldMissing: MissingFieldErrorHandler = defaultMissingFieldHandler): Parsed[String] = {
    readField(obj, fieldName, stringConverter, onParseError, onFieldMissing)
  }

  def readInt(obj: JsObject,
              fieldName: String,
              onParseError: ParseErrorHandler = defaultParseErrorHandler,
              onFieldMissing: MissingFieldErrorHandler = defaultMissingFieldHandler): Parsed[Int] = {
    readField(obj, fieldName, intConverter, onParseError, onFieldMissing)
  }

  def readUuid(obj: JsObject,
               fieldName: String,
               onParseError: ParseErrorHandler = defaultParseErrorHandler,
               onFieldMissing: MissingFieldErrorHandler = defaultMissingFieldHandler): Parsed[UUID] = {
    readField(obj, fieldName, uuidConverter, onParseError, onFieldMissing)
  }

  def readBool(obj: JsObject,
               fieldName: String,
               onParseError: ParseErrorHandler = defaultParseErrorHandler,
               onFieldMissing: MissingFieldErrorHandler = defaultMissingFieldHandler): Parsed[Boolean] = {
    readField(obj, fieldName, booleanConverter, onParseError, onFieldMissing)
  }

  def readStringOptional(obj: JsObject,
                         fieldName: String,
                         onParseError: ParseErrorHandler = defaultParseErrorHandler): Parsed[OptionalField[String]] = {
    readFieldOptional(obj, fieldName, stringConverter, onParseError)
  }

  def readIntOptional(obj: JsObject,
                      fieldName: String,
                      onParseError: ParseErrorHandler = defaultParseErrorHandler): Parsed[OptionalField[Int]] = {
    readFieldOptional(obj, fieldName, intConverter, onParseError)
  }

  def readUuidOptional(obj: JsObject,
                       fieldName: String,
                       onParseError: ParseErrorHandler = defaultParseErrorHandler): Parsed[OptionalField[UUID]] = {
    readFieldOptional(obj, fieldName, uuidConverter, onParseError)
  }

  def readBoolOptional(
      obj: JsObject,
      fieldName: String,
      onParseError: ParseErrorHandler = defaultParseErrorHandler
  ): Parsed[OptionalField[Boolean]] = {
    readFieldOptional(obj, fieldName, booleanConverter, onParseError)
  }

  def readOptionString(obj: JsObject,
                       fieldName: String,
                       onParseError: ParseErrorHandler = defaultParseErrorHandler): Parsed[Option[String]] = {
    readOptionField(obj, fieldName, stringConverter, onParseError)
  }

  def readOptionInt(obj: JsObject,
                    fieldName: String,
                    onParseError: ParseErrorHandler = defaultParseErrorHandler): Parsed[Option[Int]] = {
    readOptionField(obj, fieldName, intConverter, onParseError)
  }

  def readOptionUuid(obj: JsObject,
                     fieldName: String,
                     onParseError: ParseErrorHandler = defaultParseErrorHandler): Parsed[Option[UUID]] = {
    readOptionField(obj, fieldName, uuidConverter, onParseError)
  }

  def readOptionBool(
      obj: JsObject,
      fieldName: String,
      onParseError: ParseErrorHandler = defaultParseErrorHandler
  ): Parsed[Option[Boolean]] = {
    readOptionField(obj, fieldName, booleanConverter, onParseError)
  }

  def readObject[U: ParsingProtocol](obj: JsObject, fieldName: String): Parsed[U] = {
    readObjectField(obj, fieldName, defaultMissingFieldHandler)
  }
}
