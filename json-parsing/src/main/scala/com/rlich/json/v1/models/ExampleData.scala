package com.rlich.json.v1.models

import java.util.UUID

import cats.implicits._
import com.rlich.json.core.{OptionalField, Parsed}
import com.rlich.json.v1.core.ParsingProtocol
import com.rlich.json.v1.parsing.DefaultJsonParseSupport
import spray.json.JsValue

case class ExampleData(uuid: UUID, title: String, age: Int, version: Int, option: OptionalField[String], person: Person)

trait ExampleDataParsingProtocol {

  implicit object ExampleDataParsingFormat extends ParsingProtocol[ExampleData] with DefaultJsonParseSupport {

    import PersonParsingProtocol._

    override def read(value: JsValue): Parsed[ExampleData] = {
      val obj = value.asJsObject
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
