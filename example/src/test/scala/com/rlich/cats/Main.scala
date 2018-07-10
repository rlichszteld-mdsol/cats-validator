package com.rlich.cats

import cats.data.NonEmptyList
import cats.data.Validated.{Invalid, Valid}
import cats.implicits._
import com.rlich.cats.models.Data
import com.rlich.cats.parsing.JsonProcessor
import com.rlich.cats.validation.BusinessValidator

object Main extends App {
  import spray.json._

  val validJson =
    """
      |{
      | "uuid": "4a1eb5a3-447e-4143-a00d-bba7d926fd3f",
      | "title": "Some long title",
      | "version": 2,
      | "age": 15
      |}
    """.stripMargin
  val validObj = validJson.parseJson.asJsObject

  val invalidJson =
    """
      |{
      | "uuid": "4a1eb5a3-447e-4143-a00d-bba7d926fd3f",
      | "titleX": "Some title",
      | "version": "foo",
      | "age": 18
      |}
    """.stripMargin
  val invalidObj = invalidJson.parseJson.asJsObject
  processJson(validObj)

  private def processJson(obj: JsObject): Unit = {
    val result =
      (
        JsonProcessor.readUuid(obj, "uuid"),
        JsonProcessor.readString(obj, "title"),
        JsonProcessor.readInt(obj, "age"),
        JsonProcessor.readInt(obj, "version")
      ).mapN(Data)
        .andThen(BusinessValidator.validateData)
    result match {
      case Valid(data) => println(s".Data: $data")
      case Invalid(nel: NonEmptyList[_]) =>
        println("Validation errors:")
        nel.toList.foreach(error => println(s" - $error"))
    }
  }

}
