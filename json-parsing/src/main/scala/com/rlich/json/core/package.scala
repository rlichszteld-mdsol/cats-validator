package com.rlich.json

import cats.data.ValidatedNel
import spray.json.JsValue

package object core {
  trait ParsingError

  type OptionalField[T] = Either[Unit.type, T]
  type Parsed[T] = ValidatedNel[ParsingError, T]
  type ParseErrorHandler = (String, JsValue) => ParsingError
  type MissingFieldErrorHandler = String => ParsingError

  type JsConverter[T] = PartialFunction[JsValue, T]

  trait ParsingProtocol[T] {
    def read(value: JsValue): Parsed[T]
  }
}
