package com.example

import com.example.CatsHelper._
import com.example.validation.{BusinessValidation, TitleValidationError}
import org.scalatest.{Matchers, WordSpec}

class BusinessValidatorTest extends WordSpec with Matchers {
  "#validateTitle" should {
    "return Valid if title is valid" in {
      val title = "some title"
      val result = BusinessValidation.validateTitle(title)

      result shouldBeValid title
    }

    "return Invalid if title is not valid" in {
      val title = "some long title"
      val result = BusinessValidation.validateTitle(title)

      result shouldBeError TitleValidationError(title)
    }
  }
}
