package com.rlich.cats.parsing

import java.util.UUID

import cats.implicits._
import com.rlich.cats.utils.UUIDParser
import com.rlich.cats.validation.{InvalidFieldTypeError, MissingFieldError, Parsing}
import spray.json.{JsNumber, JsObject, JsString, JsValue}

import scala.reflect.ClassTag
import scala.util.{Failure, Success, Try}

object JsValueConverters {
  def stringConverter: JsString => String = _.value

  def intConverter: JsNumber => Int = _.value.toInt

  def uuidConverter: JsString => UUID =
    string => UUIDParser.parseString(string.value)
}

object JsonProcessor extends Parsing {

  def processField[T <: JsValue: Manifest, U](obj: JsObject, fieldName: String, parse: T => U)(
      implicit requestedType: ClassTag[T]
  ): Parsed[U] = {
    obj.fields.get(fieldName) match {
      case Some(field) =>
        Try(manifest[T].runtimeClass.cast(field).asInstanceOf[T]) match {
          case Success(value) => parse(value).validNel
          case Failure(_: ClassCastException) =>
            InvalidFieldTypeError(fieldName, requestedType.runtimeClass.getSimpleName, field.getClass.getSimpleName).invalidNel
          case Failure(e) => throw e
        }
      case _ => MissingFieldError(fieldName).invalidNel
    }
  }

  def readString(obj: JsObject, fieldName: String): Parsed[String] = {
    processField(obj, fieldName, JsValueConverters.stringConverter)
  }

  def readInt(obj: JsObject, fieldName: String): Parsed[Int] = {
    processField(obj, fieldName, JsValueConverters.intConverter)
  }

  def readUuid(obj: JsObject, fieldName: String): Parsed[UUID] = {
    processField(obj, fieldName, JsValueConverters.uuidConverter)
  }
}
