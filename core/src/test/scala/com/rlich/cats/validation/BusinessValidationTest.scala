package com.rlich.cats.validation

import org.scalatest.{Matchers, WordSpec}
import com.rlich.cats.CatsHelper._

class BusinessValidationTest extends WordSpec with Matchers with BusinessValidation {

  "default validation" should {
    "pass original value through" in {
      val data = "boo"

      accept(data) shouldBeValid data
    }
  }
}
