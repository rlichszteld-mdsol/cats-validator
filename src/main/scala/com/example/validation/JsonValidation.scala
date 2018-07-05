package com.example.validation

trait JsonValidationError extends ValidationError

case class MissingFieldError(fieldName: String) extends JsonValidationError {
  override val message: String = s"'$fieldName' is missing!"
}

case class InvalidFieldTypeError(fieldName: String, expectedType: String, actualType: String)
    extends JsonValidationError {
  override val message: String = s"'$fieldName:$actualType' is not of the required type '$expectedType'!"
}
