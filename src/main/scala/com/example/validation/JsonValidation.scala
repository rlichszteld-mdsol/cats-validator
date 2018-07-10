package com.example.validation

trait JsonParsingError extends ParsingError

case class MissingFieldError(fieldName: String) extends JsonParsingError {
  override val message: String = s"'$fieldName' is missing!"
}

case class InvalidFieldTypeError(fieldName: String, expectedType: String, actualType: String) extends JsonParsingError {
  override val message: String = s"'$fieldName:$actualType' is not of the required type '$expectedType'!"
}
