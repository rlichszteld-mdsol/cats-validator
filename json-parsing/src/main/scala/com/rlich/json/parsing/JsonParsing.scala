package com.rlich.json.parsing

import cats.data.ValidatedNel
import cats.implicits._
import com.rlich.json.parsing.JsonParsing._
import spray.json.{JsObject, JsValue}

package object JsonParsing {
  trait ParsingError

  type OptionalField[T] = Either[Unit.type, T]
  type Parsed[T] = ValidatedNel[ParsingError, T]
  type ParseErrorHandler = (String, JsValue) => ParsingError

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
                             onFieldMissing: String => ParsingError): Parsed[U] = {
    obj.fields.get(fieldName) match {
      case Some(value: JsValue) =>
        parse.lift(value).map(_.validNel).getOrElse(onParseError(fieldName, value).invalidNel)
      case None => onFieldMissing(fieldName).invalidNel
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
}

object JsonParser {
  def parse[T: ParsingProtocol](obj: JsObject): Parsed[T] = {
    implicitly[ParsingProtocol[T]].read(obj)
  }

  implicit class JsonParserOps(obj: JsObject) {
    def parseAs[T: ParsingProtocol]: Parsed[T] = JsonParser.parse(obj)
  }
}
