package com.example

import org.scalatest.{Matchers, WordSpec}
import CatsHelper._
import com.example.validation.BusinessValidation

class ValidatorTest extends WordSpec with Matchers {

  "default validation" should {
    "pass original value through" in {
      val data = "boo"

      BusinessValidation.passthroughValidation(data) shouldBeValid data
    }
  }
}
