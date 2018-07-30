package com.rlich.json.v1.models

import cats.implicits._
import com.rlich.json.core.{OptionalField, Parsed, ParsingProtocol}
import com.rlich.json.v1.parsing.DefaultJsonParseSupport
import spray.json.JsValue

case class Person(name: String, nickname: OptionalField[String], age: Option[Int])

trait PersonParsingProtocol {

  implicit object PersonParsingFormat extends ParsingProtocol[Person] with DefaultJsonParseSupport {

    override def read(value: JsValue): Parsed[Person] = {
      val obj = value.asJsObject
      (
        readString(obj, "name"),
        readStringOptional(obj, "nickname"),
        readOptionInt(obj, "age")
      ).mapN(Person)
    }
  }
}

object PersonParsingProtocol extends PersonParsingProtocol
