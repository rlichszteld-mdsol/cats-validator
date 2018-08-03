package com.rlich.json.v2.models

import cats.implicits._
import com.rlich.json.core.Parsed
import com.rlich.json.v2.core.{ParseValueErrorHandler, ParsingProtocol}
import com.rlich.json.v2.parsing.DefaultJsonParseSupport
import spray.json.JsValue

case class ChildModel(field1: String, field2: Int)

trait ChildModelParsingProtocol {

  implicit object ChildModelParsingFormat extends ParsingProtocol[ChildModel] with DefaultJsonParseSupport {

    def read(value: JsValue)(implicit
                             onParseError: ParseValueErrorHandler): Parsed[ChildModel] = {
      val obj = value.asJsObject
      (
        readString(obj, "field1"),
        readInt(obj, "field2"),
      ).mapN(ChildModel)
    }
  }

}

object ChildModelParsingProtocol extends ChildModelParsingProtocol
