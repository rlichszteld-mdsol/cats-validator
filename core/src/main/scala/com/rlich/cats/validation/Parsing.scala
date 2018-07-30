package com.rlich.cats.validation

import com.rlich.cats.validation.common.{Validation, ValidationError}

trait ParsingError extends ValidationError

trait Parsing extends Validation {
  type Parsed[A] = ValidationResult[ParsingError, A]
}
