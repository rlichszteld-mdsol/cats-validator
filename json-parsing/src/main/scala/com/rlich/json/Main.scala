package com.rlich.json

import cats.data.NonEmptyList
import cats.data.Validated.{Invalid, Valid}
import com.rlich.json.core.Parsed
import com.rlich.json.v1.models.ExampleData
import com.rlich.json.v2.models.TestModel
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
      |   "name": "Johny Cash",
      |   "age": null
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

  {
    import com.rlich.json.v1.parsing.JsonParser._
    import com.rlich.json.v1.models.ExampleDataParsingProtocol._

    val validResult = com.rlich.json.v1.parsing.JsonParser.parse[ExampleData](validObj)
    processResult(validResult)

    val invalidResult = invalidObj.parseAs[ExampleData]
    processResult(invalidResult)
  }

  val v2json =
    """
      | {
      |   "string_data": "A text",
      |   "int_data": 2018,
      |   "bool_data": true,
      |   "optional_field": null,
      |   "child": {
      |     "field1": "foo",
      |     "field2": 3
      |   }
      | }
    """.stripMargin
  val v2obj = v2json.parseJson.asJsObject

  {
    import com.rlich.json.v2.parsing.JsonParser._
    import com.rlich.json.v2.parsing.DefaultJsonParseSupport._
    import com.rlich.json.v2.models.TestModelParsingProtocol._

    val v2Result = v2obj.parseAs[TestModel](defaultParseErrorHandler)
    processResult(v2Result)
  }
}
