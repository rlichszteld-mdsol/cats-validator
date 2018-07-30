package com.rlich.json.v2.parsing

import java.util.UUID

import cats.implicits._
import com.rlich.json.core.CommonErrors.{FieldTypeError, MissingFieldError}
import com.rlich.json.core.{MissingFieldErrorHandler, ParseErrorHandler, Parsed, ParsingProtocol}
import com.rlich.json.utils.UUIDParser
import spray.json.{JsBoolean, JsNull, JsNumber, JsObject, JsString, JsValue}

trait CommonProtocols {
  implicit object StringProtocol extends ParsingProtocol[String] {

    def read(value: JsValue): Parsed[String] = value match {
      case JsString(x) => x.validNel
    }
  }

  implicit object IntProtocol extends ParsingProtocol[Int] {

    def read(value: JsValue): Parsed[Int] = value match {
      case JsNumber(x) => x.intValue.validNel
    }
  }

  implicit object BoolProtocol extends ParsingProtocol[Boolean] {

    def read(value: JsValue): Parsed[Boolean] = value match {
      case JsBoolean(x) => x.booleanValue.validNel
    }
  }

  implicit object UUIDProtocol extends ParsingProtocol[UUID] {

    def read(value: JsValue): Parsed[UUID] = value match {
      case JsString(x) => UUIDParser.parseString(x).validNel
    }
  }

  class OptionProtocol[T: ParsingProtocol] extends ParsingProtocol[Option[T]] {

    def read(value: JsValue): Parsed[Option[T]] = value match {
      case JsNull => None.validNel
      case x => implicitly[ParsingProtocol[T]].read(x).map(Option(_))
    }
  }

  implicit def optionProtocol[T: ParsingProtocol]: OptionProtocol[T] = new OptionProtocol[T]
}

trait DefaultJsonParseSupport extends JsonParseSupport with CommonProtocols {
  def defaultMissingFieldHandler: MissingFieldErrorHandler = fieldName => MissingFieldError(fieldName)

  def defaultParseErrorHandler: ParseErrorHandler = (fieldName, value) => FieldTypeError(fieldName, value)

  def readString(obj: JsObject,
                 fieldName: String,
                 onParseError: ParseErrorHandler = defaultParseErrorHandler,
                 onFieldMissing: MissingFieldErrorHandler = defaultMissingFieldHandler): Parsed[String] = {
    readField[String](obj, fieldName, onFieldMissing)
  }

  def readInt(obj: JsObject,
              fieldName: String,
              onParseError: ParseErrorHandler = defaultParseErrorHandler,
              onFieldMissing: MissingFieldErrorHandler = defaultMissingFieldHandler): Parsed[Int] = {
    readField[Int](obj, fieldName, onFieldMissing)
  }

  def readUuid(obj: JsObject,
               fieldName: String,
               onParseError: ParseErrorHandler = defaultParseErrorHandler,
               onFieldMissing: MissingFieldErrorHandler = defaultMissingFieldHandler): Parsed[UUID] = {
    readField[UUID](obj, fieldName, onFieldMissing)
  }

  def readBool(obj: JsObject,
               fieldName: String,
               onParseError: ParseErrorHandler = defaultParseErrorHandler,
               onFieldMissing: MissingFieldErrorHandler = defaultMissingFieldHandler): Parsed[Boolean] = {
    readField[Boolean](obj, fieldName, onFieldMissing)
  }
}
