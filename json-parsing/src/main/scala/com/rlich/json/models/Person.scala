package com.rlich.json.models

import com.rlich.json.parsing.DefaultJsonParseSupport
import com.rlich.json.parsing.JsonParsing.{OptionalField, Parsed, ParsingProtocol}
import spray.json.JsObject
import cats.implicits._

case class Person(name: String, nickname: OptionalField[String], age: Option[Int])

trait PersonParsingProtocol {

  implicit object PersonParsingFormat extends ParsingProtocol[Person] with DefaultJsonParseSupport {

    override def read(obj: JsObject): Parsed[Person] = {
      (
        readString(obj, "name"),
        readStringOptional(obj, "nickname"),
        readOptionInt(obj, "age")
      ).mapN(Person)
    }
  }
}

object PersonParsingProtocol extends PersonParsingProtocol
