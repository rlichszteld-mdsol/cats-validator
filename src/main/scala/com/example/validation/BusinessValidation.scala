package com.example.validation

import cats.implicits._
import com.example.models.Data

trait BusinessValidationError extends ValidationError

case class TitleValidationError(value: String) extends BusinessValidationError {
  override val message: String = s"Title ($value) should be between 1 and 10 characters long"
}

case class AgeValidationError(age: Int) extends BusinessValidationError {
  override val message: String = s"Age ($age) is not valid (you must be over 18 years old)"
}

case class VersionValidationError(version: Int) extends BusinessValidationError {
  override val message: String = s"Version ($version) does not seem to be valid"
}

object BusinessValidation extends Validation {

  def validateTitle(title: String): ValidationResult[String] = {
    if (title.length > 0 && title.length <= 10) title.validNel else TitleValidationError(title).invalidNel
  }

  def validateAge(age: Int): ValidationResult[Int] = {
    if (age >= 18) age.validNel else AgeValidationError(age).invalidNel
  }

  def validateVersion(version: Int): ValidationResult[Int] = {
    if (version > 0) version.validNel else VersionValidationError(version).invalidNel
  }

  def validateData(input: Data): ValidationResult[Data] = {
    (
      accept(input.uuid),
      validateTitle(input.title),
      validateAge(input.age),
      validateVersion(input.version)
    ).mapN(Data)
  }
}
