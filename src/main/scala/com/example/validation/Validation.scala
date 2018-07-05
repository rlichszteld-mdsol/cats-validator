package com.example.validation

import cats.data.ValidatedNel

trait ValidationError {
  val message: String
}

trait Validation {
  type ValidationResult[A] = ValidatedNel[ValidationError, A]

  type BusinessValidation[A] = A => ValidationResult[A]
}
