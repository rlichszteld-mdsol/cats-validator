package com.example.validation

import cats.implicits._
import com.example.models.Data
import com.example.validation.common.{Validation, ValidationError}

trait BusinessValidationError extends ValidationError

trait BusinessValidation extends Validation {
  type BusinessValidationResult[A] = ValidationResult[BusinessValidationError, A]
  type BusinessValidation[A] = A => BusinessValidationResult[A]

  def accept[U]: BusinessValidation[U] = value => value.validNel
}

case class TitleValidationError(value: String) extends BusinessValidationError {
  override val message: String = s"Title ($value) should be between 1 and 10 characters long"
}

case class AgeValidationError(age: Int) extends BusinessValidationError {
  override val message: String = s"Age ($age) is not valid (you must be over 18 years old)"
}

case class VersionValidationError(version: Int) extends BusinessValidationError {
  override val message: String = s"Version ($version) does not seem to be valid"
}

object BusinessValidation extends BusinessValidation {

  def validateTitle(title: String): BusinessValidationResult[String] = {
    if (title.length > 0 && title.length <= 10) title.validNel else TitleValidationError(title).invalidNel
  }

  def validateAge(age: Int): BusinessValidationResult[Int] = {
    if (age >= 18) age.validNel else AgeValidationError(age).invalidNel
  }

  def validateVersion(version: Int): BusinessValidationResult[Int] = {
    if (version > 0) version.validNel else VersionValidationError(version).invalidNel
  }

  def validateData(input: Data): BusinessValidationResult[Data] = {
    (
      accept(input.uuid),
      validateTitle(input.title),
      validateAge(input.age),
      validateVersion(input.version)
    ).mapN(Data)
  }
}
