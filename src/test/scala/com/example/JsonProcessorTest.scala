package com.example

import java.util.UUID

import CatsHelper._
import cats.data.ValidatedNel
import org.scalatest.{Matchers, WordSpec}
import spray.json._

class JsonProcessorTest extends WordSpec with Matchers with Validator {

  private val json =
    """
      |{
      | "newTitle": "Some title",
      | "uuid": "4a1eb5a3-447e-4143-a00d-bba7d926fd3f",
      | "version": "foo"
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
  }
}
