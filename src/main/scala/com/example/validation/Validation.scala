package com.example.validation

import cats.data.ValidatedNel
import cats.implicits._

trait ValidationError {
  val message: String

  override def toString: String = message
}

trait Validation {
  type ValidationResult[A] = ValidatedNel[ValidationError, A]

  type BusinessValidation[A] = A => ValidationResult[A]

  def accept[U]: BusinessValidation[U] = value => value.validNel
}
