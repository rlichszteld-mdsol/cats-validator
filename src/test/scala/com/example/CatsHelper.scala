package com.example

import cats.data.Validated.{Invalid, Valid}
import cats.data.{NonEmptyList, ValidatedNel}
import cats.implicits._
import com.example.validation.Validation
import org.scalatest.{Assertion, Matchers}

object CatsHelper extends Validation with Matchers {
  implicit class ValidatedNelExtension[E, R](result: ValidatedNel[E, R]) {
    def shouldBeError(error: E): Assertion = {
      result shouldBe error.invalidNel
    }

    def shouldBeValid(value: R): Assertion = {
      result shouldBe value.validNel
    }

    def shouldContain(errors: Seq[E]): Assertion = {
      result match {
        case Valid(_) => fail
        case Invalid(nel: NonEmptyList[E]) => nel.toList should contain theSameElementsAs errors
      }
    }
  }
}
