package com.rlich.json.v2

import com.rlich.json.core.{Parsed, ParsingError}
import spray.json.JsValue

package object core {
  type ParseValueErrorHandler = JsValue => ParsingError
  type ParseFieldErrorHandler = String => ParseValueErrorHandler

  type MissingFieldErrorHandler = String => ParsingError

  trait ParsingProtocol[T] {
    def read(value: JsValue)(implicit onParseError: ParseValueErrorHandler): Parsed[T]
  }
}
