package com.rlich.json
import cats.data.NonEmptyList
import cats.data.Validated.{Invalid, Valid}
import com.rlich.json.models.ExampleData
import com.rlich.json.parsing.JsonParser
import com.rlich.json.parsing.JsonParsing.Parsed
import spray.json._

object Main extends App {
  val validJson =
    """
      |{
      | "uuid": "4a1eb5a3-447e-4143-a00d-bba7d926fd3f",
      | "title": "Some long title",
      | "version": 2,
      | "age": 15,
      | "person": {
      |   "name": "Johny Cash"
      | }
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

  def processResult[T](result: Parsed[T]): Unit = {
    println()
    result match {
      case Valid(data) => println(s"Data: $data")
      case Invalid(nel: NonEmptyList[_]) =>
        println("Validation errors:")
        nel.toList.foreach(error => println(s" - $error"))
    }

  }

  import com.rlich.json.models.ExampleDataParsingProtocol._

  val validResult = JsonParser.parse[ExampleData](validObj)
  processResult(validResult)

  import JsonParser._
  val invalidResult = invalidObj.parseAs[ExampleData]
  processResult(invalidResult)
}
