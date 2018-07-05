package com.example

import cats.data.ValidatedNel

trait ValidationError {
  val message: String
}

case class MissingFieldError(fieldName: String) extends ValidationError {
  override val message: String = s"'$fieldName' is missing!"
}

case class InvalidFieldTypeError(fieldName: String, expectedType: String, actualType: String) extends ValidationError {
  override val message = s"'$fieldName:$actualType' is not of the required type '$expectedType'!"
}

trait Validator {
  type ValidationResult[A] = ValidatedNel[ValidationError, A]
}
