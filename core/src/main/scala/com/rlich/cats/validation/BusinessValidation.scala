package com.rlich.cats.validation

import cats.implicits._
import com.rlich.cats.validation.common.{Validation, ValidationError}

trait BusinessValidationError extends ValidationError

trait BusinessValidation extends Validation {
  type BusinessValidationResult[A] = ValidationResult[BusinessValidationError, A]
  type BusinessValidation[A] = A => BusinessValidationResult[A]

  def accept[U]: BusinessValidation[U] = value => value.validNel
}
