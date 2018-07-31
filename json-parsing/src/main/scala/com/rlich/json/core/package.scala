package com.rlich.json

import cats.data.ValidatedNel
import spray.json.JsValue

package object core {
  trait ParsingError

  type OptionalField[T] = Either[Unit.type, Option[T]]
  type Parsed[T] = ValidatedNel[ParsingError, T]
}
