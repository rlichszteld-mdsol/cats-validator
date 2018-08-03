package com.rlich.json.v2.parsing

import cats.implicits._
import com.rlich.json.core._
import com.rlich.json.v2.core.{MissingFieldErrorHandler, ParseFieldErrorHandler, ParsingProtocol}
import spray.json.JsObject

trait JsonParseSupport {
  protected def readFieldOptional[U](
      obj: JsObject,
      fieldName: String
  )(implicit pp: ParsingProtocol[U], onParseError: ParseFieldErrorHandler): Parsed[OptionalField[U]] = {
    obj.fields.get(fieldName) match {
      case Some(value) =>
        pp.read(value)(onParseError(fieldName))
          .map(maybeValue => Right(Option(maybeValue)))
          .orElse(onParseError(fieldName)(value).invalidNel)
      case None => Left(Unit).validNel
    }
  }

  protected def readObjectField[U](obj: JsObject, fieldName: String)(
      implicit pp: ParsingProtocol[U],
      onFieldMissing: MissingFieldErrorHandler,
      onParseError: ParseFieldErrorHandler
  ): Parsed[U] = {
    obj.fields.get(fieldName) match {
      case Some(jsObject: JsObject) => pp.read(jsObject)(onParseError(fieldName))
      case None => onFieldMissing(fieldName).invalidNel
      case Some(value) => onParseError(fieldName)(value).invalidNel
    }
  }

  protected def readField[U](obj: JsObject, fieldName: String)(implicit
                                                               pp: ParsingProtocol[U],
                                                               onFieldMissing: MissingFieldErrorHandler,
                                                               onParseError: ParseFieldErrorHandler): Parsed[U] = {
    obj.fields.get(fieldName) match {
      case Some(field) => pp.read(field)(onParseError(fieldName))
      case None => onFieldMissing(fieldName).invalidNel
    }
  }
}

object JsonParser {
  def parse[T: ParsingProtocol](obj: JsObject)(implicit onParseError: ParseFieldErrorHandler): Parsed[T] = {
    implicitly[ParsingProtocol[T]].read(obj)(onParseError(""))
  }

  implicit class JsonParserOps(obj: JsObject) {
    def parseAs[T: ParsingProtocol](implicit onParseError: ParseFieldErrorHandler): Parsed[T] =
      JsonParser.parse(obj)
  }
}
