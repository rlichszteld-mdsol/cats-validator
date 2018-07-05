package com.example.validation

import cats.implicits._

trait BusinessValidationError extends ValidationError

case class TitleValidationError(value: String) extends BusinessValidationError {
  override val message: String = s"Title ($value) should be between 1 and 10 characters long"
}

case class AgeValidationError(age: Int) extends BusinessValidationError {
  override val message: String = s"Age ($age) is not valid (you must be over 18 years old)"
}

object BusinessValidation extends Validation {
  def passthroughValidation[U]: BusinessValidation[U] = value => value.validNel

  def validateTitle(title: String): ValidationResult[String] = {
    if (title.length > 0 && title.length <= 10) title.validNel else TitleValidationError(title).invalidNel
  }

  def validateAge(age: Int): ValidationResult[Int] = {
    if (age >= 18) age.validNel else AgeValidationError(age).invalidNel
  }
}
