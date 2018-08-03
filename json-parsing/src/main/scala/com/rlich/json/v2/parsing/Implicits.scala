package com.rlich.json.v2.parsing
import com.rlich.json.core.CommonErrors.{FieldTypeError, MissingFieldError}
import com.rlich.json.v2.core.{MissingFieldErrorHandler, ParseFieldErrorHandler}

object Implicits {

  object ErrorHandlers {
    implicit def defaultMissingFieldHandler: MissingFieldErrorHandler = fieldName => MissingFieldError(fieldName)

    implicit def defaultParseErrorHandler: ParseFieldErrorHandler =
      fieldName => value => FieldTypeError(fieldName, value)
  }
}
