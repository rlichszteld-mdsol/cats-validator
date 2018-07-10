package com.example.validation.common

import cats.data.ValidatedNel

trait ValidationError {
  val message: String

  override def toString: String = message
}

trait Validation {
  type ValidationResult[E, A] = ValidatedNel[E, A]
}
