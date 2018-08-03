package com.rlich.json.v2.parsing

import java.util.UUID

import cats.implicits._
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

    def read(value: JsValue)(implicit onParseError: ParseValueErrorHandler): Parsed[String] = value match {
      case JsString(x) => x.validNel
      case x => onParseError(x).invalidNel
    }
  }

  implicit object IntProtocol extends ParsingProtocol[Int] {

    def read(value: JsValue)(implicit onParseError: ParseValueErrorHandler): Parsed[Int] = value match {
      case JsNumber(x) => x.intValue.validNel
      case x => onParseError(x).invalidNel
    }
  }

  implicit object BoolProtocol extends ParsingProtocol[Boolean] {

    def read(value: JsValue)(implicit onParseError: ParseValueErrorHandler): Parsed[Boolean] = value match {
      case JsBoolean(x) => x.booleanValue.validNel
      case x => onParseError(x).invalidNel
    }
  }

  implicit object UUIDProtocol extends ParsingProtocol[UUID] {

    def read(value: JsValue)(implicit onParseError: ParseValueErrorHandler): Parsed[UUID] = value match {
      case JsString(x) => UUIDParser.parseString(x).validNel
      case x => onParseError(x).invalidNel
    }
  }

  class OptionProtocol[T: ParsingProtocol] extends ParsingProtocol[Option[T]] {

    def read(value: JsValue)(implicit onParseError: ParseValueErrorHandler): Parsed[Option[T]] = value match {
      case JsNull => None.validNel
      case x => implicitly[ParsingProtocol[T]].read(x)(onParseError).map(Option(_))
    }
  }

  implicit def optionProtocol[T: ParsingProtocol]: OptionProtocol[T] = new OptionProtocol[T]
}

trait DefaultJsonParseSupport extends JsonParseSupport with CommonProtocols {
  import Implicits.ErrorHandlers._

  def readString(
      obj: JsObject,
      fieldName: String,
      onFieldMissing: MissingFieldErrorHandler = defaultMissingFieldHandler,
      onParseError: ParseFieldErrorHandler = defaultParseErrorHandler
  )(implicit pp: ParsingProtocol[String]): Parsed[String] = {
    readField[String](obj, fieldName)(pp, onFieldMissing, onParseError)
  }

  def readInt(
      obj: JsObject,
      fieldName: String,
      onFieldMissing: MissingFieldErrorHandler = defaultMissingFieldHandler,
      onParseError: ParseFieldErrorHandler = defaultParseErrorHandler
  )(implicit pp: ParsingProtocol[Int]): Parsed[Int] = {
    readField[Int](obj, fieldName)(pp, onFieldMissing, onParseError)
  }

  def readUuid(
      obj: JsObject,
      fieldName: String,
      onFieldMissing: MissingFieldErrorHandler = defaultMissingFieldHandler,
      onParseError: ParseFieldErrorHandler = defaultParseErrorHandler
  )(implicit pp: ParsingProtocol[UUID]): Parsed[UUID] = {
    readField[UUID](obj, fieldName)(pp, onFieldMissing, onParseError)
  }

  def readBool(
      obj: JsObject,
      fieldName: String,
      onFieldMissing: MissingFieldErrorHandler = defaultMissingFieldHandler,
      onParseError: ParseFieldErrorHandler = defaultParseErrorHandler
  )(implicit pp: ParsingProtocol[Boolean]): Parsed[Boolean] = {
    readField[Boolean](obj, fieldName)(pp, onFieldMissing, onParseError)
  }

  def readObject[U](
      obj: JsObject,
      fieldName: String,
      onFieldMissing: MissingFieldErrorHandler = defaultMissingFieldHandler,
      onParseError: ParseFieldErrorHandler = defaultParseErrorHandler
  )(implicit pp: ParsingProtocol[U]): Parsed[U] = {
    readField[U](obj, fieldName)(pp, onFieldMissing, onParseError)
  }
}

object DefaultJsonParseSupport extends DefaultJsonParseSupport
