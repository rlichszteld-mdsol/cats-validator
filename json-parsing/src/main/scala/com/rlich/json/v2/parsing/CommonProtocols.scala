package com.rlich.json.v2.parsing

import java.util.UUID

import cats.implicits._
import com.rlich.json.core.CommonErrors.{FieldTypeError, MissingFieldError}
import com.rlich.json.core._
import com.rlich.json.utils.UUIDParser
import com.rlich.json.v2.core.{
  MissingFieldErrorHandler,
  ParseFieldErrorHandler,
  ParseValueErrorHandler,
  ParsingProtocol
}
import spray.json.{JsBoolean, JsNull, JsNumber, JsObject, JsString, JsValue}

trait CommonProtocols {
  implicit object StringProtocol extends ParsingProtocol[String] {

    def read(value: JsValue)(onParseError: ParseValueErrorHandler): Parsed[String] = value match {
      case JsString(x) => x.validNel
      case x => onParseError(x).invalidNel
    }
  }

  implicit object IntProtocol extends ParsingProtocol[Int] {

    def read(value: JsValue)(onParseError: ParseValueErrorHandler): Parsed[Int] = value match {
      case JsNumber(x) => x.intValue.validNel
      case x => onParseError(x).invalidNel
    }
  }

  implicit object BoolProtocol extends ParsingProtocol[Boolean] {

    def read(value: JsValue)(onParseError: ParseValueErrorHandler): Parsed[Boolean] = value match {
      case JsBoolean(x) => x.booleanValue.validNel
      case x => onParseError(x).invalidNel
    }
  }

  implicit object UUIDProtocol extends ParsingProtocol[UUID] {

    def read(value: JsValue)(onParseError: ParseValueErrorHandler): Parsed[UUID] = value match {
      case JsString(x) => UUIDParser.parseString(x).validNel
      case x => onParseError(x).invalidNel
    }
  }

  class OptionProtocol[T: ParsingProtocol] extends ParsingProtocol[Option[T]] {

    def read(value: JsValue)(onParseError: ParseValueErrorHandler): Parsed[Option[T]] = value match {
      case JsNull => None.validNel
      case x => implicitly[ParsingProtocol[T]].read(x)(onParseError).map(Option(_))
    }
  }

  implicit def optionProtocol[T: ParsingProtocol]: OptionProtocol[T] = new OptionProtocol[T]
}

trait DefaultJsonParseSupport extends JsonParseSupport with CommonProtocols {
  def defaultMissingFieldHandler: MissingFieldErrorHandler = fieldName => MissingFieldError(fieldName)

  def defaultParseErrorHandler: ParseFieldErrorHandler = fieldName => value => FieldTypeError(fieldName, value)

  def readString(obj: JsObject,
                 fieldName: String,
                 onFieldMissing: MissingFieldErrorHandler = defaultMissingFieldHandler,
                 onParseError: ParseFieldErrorHandler = defaultParseErrorHandler): Parsed[String] = {
    readField[String](obj, fieldName, onFieldMissing, onParseError)
  }

  def readInt(obj: JsObject,
              fieldName: String,
              onFieldMissing: MissingFieldErrorHandler = defaultMissingFieldHandler,
              onParseError: ParseFieldErrorHandler = defaultParseErrorHandler): Parsed[Int] = {
    readField[Int](obj, fieldName, onFieldMissing, onParseError)
  }

  def readUuid(obj: JsObject,
               fieldName: String,
               onFieldMissing: MissingFieldErrorHandler = defaultMissingFieldHandler,
               onParseError: ParseFieldErrorHandler = defaultParseErrorHandler): Parsed[UUID] = {
    readField[UUID](obj, fieldName, onFieldMissing, onParseError)
  }

  def readBool(obj: JsObject,
               fieldName: String,
               onFieldMissing: MissingFieldErrorHandler = defaultMissingFieldHandler,
               onParseError: ParseFieldErrorHandler = defaultParseErrorHandler): Parsed[Boolean] = {
    readField[Boolean](obj, fieldName, onFieldMissing, onParseError)
  }

  def readObject[U](
      obj: JsObject,
      fieldName: String,
      onFieldMissing: MissingFieldErrorHandler = defaultMissingFieldHandler,
      onParseError: ParseFieldErrorHandler = defaultParseErrorHandler
  )(implicit pp: ParsingProtocol[U]): Parsed[U] = {
    readField[U](obj, fieldName, onFieldMissing, onParseError)
  }
}

object DefaultJsonParseSupport extends DefaultJsonParseSupport
