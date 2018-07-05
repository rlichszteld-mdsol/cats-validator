package com.example

import java.util.UUID

import spray.json.{JsNumber, JsObject, JsString, JsValue}

import scala.reflect.ClassTag
import scala.util.{Failure, Success, Try}
import cats.implicits._
import com.example.validation.{InvalidFieldTypeError, MissingFieldError, Validation}
import com.example.validation.BusinessValidation._

object JsValueConverters {
  def stringConverter: JsString => String = _.value

  def intConverter: JsNumber => Int = _.value.toInt

  def uuidConverter: JsString => UUID =
    string => UUIDParser.parseString(string.value)
}

object JsonProcessor extends Validation {

  def processField[T <: JsValue: Manifest, U](obj: JsObject,
                                              fieldName: String,
                                              parse: T => U,
                                              validate: BusinessValidation[U] = passthroughValidation[U])(
      implicit requestedType: ClassTag[T]
  ): ValidationResult[U] = {
    obj.fields.get(fieldName) match {
      case Some(field) =>
        Try(manifest[T].runtimeClass.cast(field).asInstanceOf[T]) match {
          case Success(value) => parse(value).validNel.andThen(validate)
          case Failure(_: ClassCastException) =>
            InvalidFieldTypeError(fieldName, requestedType.runtimeClass.getSimpleName, field.getClass.getSimpleName).invalidNel
          case Failure(e) => throw e
        }
      case _ => MissingFieldError(fieldName).invalidNel
    }
  }

  def readString(obj: JsObject,
                 fieldName: String,
                 validate: BusinessValidation[String] = passthroughValidation[String]): ValidationResult[String] = {
    processField(obj, fieldName, JsValueConverters.stringConverter, validate)
  }

  def readInt(obj: JsObject,
              fieldName: String,
              validate: BusinessValidation[Int] = passthroughValidation[Int]): ValidationResult[Int] = {
    processField(obj, fieldName, JsValueConverters.intConverter, validate)
  }

  def readUuid(obj: JsObject,
               fieldName: String,
               validate: BusinessValidation[UUID] = passthroughValidation[UUID]): ValidationResult[UUID] = {
    processField(obj, fieldName, JsValueConverters.uuidConverter, validate)
  }
}
