package com.rlich.cats

import java.util.UUID

import com.rlich.cats.CatsHelper._
import com.rlich.cats.parsing.JsonProcessor
import com.rlich.cats.validation.{InvalidFieldTypeError, MissingFieldError}
import com.rlich.cats.validation.common.Validation
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
      val result = JsonProcessor.processField(jsObject, "boo", (x: JsValue) => x.toString())

      result shouldBeError MissingFieldError("boo")
    }

    "return InvalidFieldTypeError when field is not of the requested type" in {
      val result = JsonProcessor.processField(jsObject, "version", (x: JsNumber) => x.value.toInt)

      result shouldBeError InvalidFieldTypeError("version", "JsNumber", "JsString")
    }

    "return parsed value using provided conversion" in {
      val value = JsonProcessor.processField(jsObject, "uuid", (x: JsString) => UUID.fromString(x.value))
      value shouldBeValid UUID.fromString("4a1eb5a3-447e-4143-a00d-bba7d926fd3f")
    }

  }
}
