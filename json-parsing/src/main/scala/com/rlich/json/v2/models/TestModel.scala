package com.rlich.json.v2.models

import com.rlich.json.core.{OptionalField, Parsed, ParsingProtocol}
import com.rlich.json.v2.parsing.DefaultJsonParseSupport
import spray.json.JsValue
import cats.implicits._

case class TestModel(stringData: String,
                     intData: Int,
                     boolData: Boolean,
                     optionalField: OptionalField[Option[String]],
                     child: Option[ChildModel])

trait TestModelParsingProtocol {

  implicit object TestModelParsingFormat extends ParsingProtocol[TestModel] with DefaultJsonParseSupport {

    import ChildModelParsingProtocol._

    def read(value: JsValue): Parsed[TestModel] = {
      val obj = value.asJsObject
      (
        readString(obj, "string_data"),
        readInt(obj, "int_data"),
        readBool(obj, "bool_data"),
        readFieldOptional[Option[String]](obj, "optional_field", defaultParseErrorHandler),
        readOptionField[ChildModel](obj, "child", defaultParseErrorHandler)
      ).mapN(TestModel)
    }
  }

}

object TestModelParsingProtocol extends TestModelParsingProtocol
