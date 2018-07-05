package com.example

import java.util.UUID

import com.example.CatsHelper._
import com.example.models.Data
import com.example.validation.{AgeValidationError, BusinessValidation, TitleValidationError, VersionValidationError}
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

  "#validateData" should {
    "return valid object if all data is correct" in {
      val input = Data(uuid = UUID.randomUUID(), title = "Some title", age = 25, version = 2)
      val result = BusinessValidation.validateData(input)

      result shouldBeValid input
    }

    "return list of errors validation fails" in {
      val input = Data(uuid = UUID.randomUUID(), title = "Some long title", age = 13, version = 0)
      val result = BusinessValidation.validateData(input)

      result shouldContain Seq(TitleValidationError(input.title),
                               AgeValidationError(input.age),
                               VersionValidationError(input.version))
    }
  }
}
