package com.rlich.json.models

import java.util.UUID

import cats.implicits._
import com.rlich.json.parsing.DefaultJsonParseSupport
import com.rlich.json.parsing.JsonParsing._
import spray.json.JsObject

case class ExampleData(uuid: UUID, title: String, age: Int, version: Int, option: OptionalField[String], person: Person)

trait ExampleDataParsingProtocol {

  implicit object ExampleDataParsingFormat extends ParsingProtocol[ExampleData] with DefaultJsonParseSupport {

    import PersonParsingProtocol._

    override def read(obj: JsObject): Parsed[ExampleData] = {
      (
        readUuid(obj, "uuid"),
        readString(obj, "title"),
        readInt(obj, "age"),
        readInt(obj, "version"),
        readStringOptional(obj, "option"),
        readObject[Person](obj, "person")
      ).mapN(ExampleData)
    }
  }
}

object ExampleDataParsingProtocol extends ExampleDataParsingProtocol
