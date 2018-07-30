package com.rlich.json.v2.models

import com.rlich.json.core.{OptionalField, Parsed, ParsingProtocol}
import com.rlich.json.v2.parsing.DefaultJsonParseSupport
import spray.json.JsValue
import cats.implicits._

case class ChildModel(field1: String, field2: Int)

trait ChildModelParsingProtocol {

  implicit object ChildModelParsingFormat extends ParsingProtocol[ChildModel] with DefaultJsonParseSupport {

    def read(value: JsValue): Parsed[ChildModel] = {
      val obj = value.asJsObject
      (
        readString(obj, "field1"),
        readInt(obj, "field2"),
      ).mapN(ChildModel)
    }
  }

}

object ChildModelParsingProtocol extends ChildModelParsingProtocol
