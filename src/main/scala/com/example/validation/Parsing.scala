package com.example.validation

import cats.data.ValidatedNel
import com.example.validation.common.ValidationError

trait ParsingError extends ValidationError

trait Parsing {
  type Parsed[A] = ValidatedNel[ParsingError, A]
}
