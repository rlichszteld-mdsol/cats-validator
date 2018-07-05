package com.example

import java.util.UUID

import CatsHelper._
import cats.data.ValidatedNel
import com.example.validation._
import org.scalatest.{Matchers, WordSpec}
import spray.json._

class JsonProcessorTest extends WordSpec with Matchers with Validation {

  private val json =
    """
      |{
      | "title": "Some title",
      | "uuid": "4a1eb5a3-447e-4143-a00d-bba7d926fd3f",
      | "version": "foo",
      | "age": 15
      |}
    """.stripMargin
  private val jsObject = json.parseJson.asJsObject

  "#processField" should {
    "return MissingFieldError when field does not exist" in {
      val result: ValidatedNel[ValidationError, String] =
        JsonProcessor.processField(jsObject, "boo", (x: JsValue) => x.toString())

      result shouldBeError MissingFieldError("boo")
    }

    "return InvalidFieldTypeError when field is not of the requested type" in {
      val result: ValidatedNel[ValidationError, Int] =
        JsonProcessor.processField(jsObject, "version", (x: JsNumber) => x.value.toInt)

      result shouldBeError InvalidFieldTypeError("version", "JsNumber", "JsString")
    }

    "return parsed value using provided conversion" in {
      val value = JsonProcessor.processField(jsObject, "uuid", (x: JsString) => UUID.fromString(x.value))
      value shouldBeValid UUID.fromString("4a1eb5a3-447e-4143-a00d-bba7d926fd3f")
    }

    "chain business validation after successful parsing and provide value on success" in {
      val result = JsonProcessor.readString(jsObject, "title", BusinessValidation.validateTitle)
      result shouldBeValid "Some title"
    }

    "chain business validation after successful parsing and return error on failure" in {
      val result = JsonProcessor.readInt(jsObject, "age", BusinessValidation.validateAge)
      result shouldBeError AgeValidationError(15)
    }
  }
}
