package com.rlich.cats.validation

import cats.data.ValidatedNel
import com.rlich.cats.validation.common.ValidationError

trait ParsingError extends ValidationError

trait Parsing {
  type Parsed[A] = ValidatedNel[ParsingError, A]
}
