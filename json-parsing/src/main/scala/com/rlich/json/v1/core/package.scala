package com.rlich.json.v1

import com.rlich.json.core.{Parsed, ParsingError}
import spray.json.JsValue

package object core {
  type JsConverter[T] = PartialFunction[JsValue, T]

  type ParseErrorHandler = (String, JsValue) => ParsingError
  type MissingFieldErrorHandler = String => ParsingError

  trait ParsingProtocol[T] {
    def read(value: JsValue): Parsed[T]
  }
}
