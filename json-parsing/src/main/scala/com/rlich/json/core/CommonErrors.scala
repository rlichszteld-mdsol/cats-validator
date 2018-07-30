package com.rlich.json.core

import spray.json.JsValue

object CommonErrors {
  case class MissingFieldError(fieldName: String) extends ParsingError

  case class FieldTypeError(fieldName: String, value: JsValue) extends ParsingError
}
