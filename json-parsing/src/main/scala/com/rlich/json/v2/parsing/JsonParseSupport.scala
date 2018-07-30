package com.rlich.json.v2.parsing

import cats.implicits._
import com.rlich.json.core._
import spray.json.{JsNull, JsObject, JsValue}

trait JsonParseSupport {
  protected def readOptionField[U: ParsingProtocol](obj: JsObject,
                                                    fieldName: String,
                                                    onParseError: ParseErrorHandler): Parsed[Option[U]] = {
    obj.fields.get(fieldName) match {
      case Some(anyValue) =>
        anyValue match {
          case value if value == JsNull => None.validNel
          case value => read(value).map(Option(_)).orElse(onParseError(fieldName, value).invalidNel)
        }
      case None => None.validNel
    }
  }

  protected def readFieldOptional[U: ParsingProtocol](obj: JsObject,
                                                      fieldName: String,
                                                      onParseError: ParseErrorHandler): Parsed[OptionalField[U]] = {
    obj.fields.get(fieldName) match {
      case Some(value) => read(value).map(Right(_)).orElse(onParseError(fieldName, value).invalidNel)
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

  protected def readField[U: ParsingProtocol](obj: JsObject,
                                              fieldName: String,
                                              onFieldMissing: String => ParsingError): Parsed[U] = {
    obj.fields.get(fieldName) match {
      case Some(field) => read(field)
      case None => onFieldMissing(fieldName).invalidNel
    }
  }

  protected def read[U: ParsingProtocol](value: JsValue): Parsed[U] = {
    implicitly[ParsingProtocol[U]].read(value)
  }
}
