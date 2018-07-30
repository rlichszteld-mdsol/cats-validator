package com.rlich.json.parsing

import cats.data.ValidatedNel
import cats.implicits._
import com.rlich.json.parsing.JsonParsing._
import spray.json._

package object JsonParsing {
  trait ParsingError

  type OptionalField[T] = Either[Unit.type, T]
  type Parsed[T] = ValidatedNel[ParsingError, T]
  type ParseErrorHandler = (String, JsValue) => ParsingError
  type MissingFieldErrorHandler = String => ParsingError

  type JsConverter[T] = PartialFunction[JsValue, T]

  trait ParsingProtocol[T] {
    def read(obj: JsObject): Parsed[T]
  }
}

trait JsonParseSupport {
  protected def readField[U](obj: JsObject,
                             fieldName: String,
                             parse: JsConverter[U],
                             onParseError: ParseErrorHandler,
                             onFieldMissing: MissingFieldErrorHandler): Parsed[U] = {
    obj.fields.get(fieldName) match {
      case Some(value: JsValue) =>
        parse.lift(value).map(_.validNel).getOrElse(onParseError(fieldName, value).invalidNel)
      case None => onFieldMissing(fieldName).invalidNel
    }
  }

  protected def readOptionField[U](obj: JsObject,
                                   fieldName: String,
                                   parse: JsConverter[U],
                                   onParseError: ParseErrorHandler): Parsed[Option[U]] = {
    obj.fields.get(fieldName) match {
      case Some(anyValue) =>
        anyValue match {
          case value if value == JsNull => None.validNel
          case value =>
            parse.lift(value).map(Some(_).validNel).getOrElse(onParseError(fieldName, value).invalidNel)
        }
      case None => None.validNel
    }
  }

  protected def readFieldOptional[U](obj: JsObject,
                                     fieldName: String,
                                     parse: JsConverter[U],
                                     onParseError: ParseErrorHandler): Parsed[OptionalField[U]] = {
    obj.fields.get(fieldName) match {
      case Some(value: JsValue) =>
        parse.lift(value).map(Right(_).validNel).getOrElse(onParseError(fieldName, value).invalidNel)
      case None => Left(Unit).validNel
    }
  }

  protected def readObjectField[U: ParsingProtocol](obj: JsObject,
                                                    fieldName: String,
                                                    onFieldMissing: String => ParsingError): Parsed[U] = {
    obj.fields.get(fieldName) match {
      case Some(jsObject: JsObject) => implicitly[ParsingProtocol[U]].read(jsObject)
      case None => onFieldMissing(fieldName).invalidNel
    }
  }
}

object JsonParser {
  def parse[T: ParsingProtocol](obj: JsObject): Parsed[T] = {
    implicitly[ParsingProtocol[T]].read(obj)
  }

  implicit class JsonParserOps(obj: JsObject) {
    def parseAs[T: ParsingProtocol]: Parsed[T] = JsonParser.parse(obj)
  }
}
