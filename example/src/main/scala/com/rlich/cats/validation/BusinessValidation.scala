package com.rlich.cats.validation

import java.util.UUID

import cats.implicits._
import com.rlich.cats.models.Data

case class TitleValidationError(value: String) extends BusinessValidationError {
  override val message: String = s"Title ($value) should be between 1 and 10 characters long"
}

case class AgeValidationError(age: Int) extends BusinessValidationError {
  override val message: String = s"Age ($age) is not valid (you must be over 18 years old)"
}

case class VersionValidationError(version: Int) extends BusinessValidationError {
  override val message: String = s"Version ($version) does not seem to be valid"
}

case class UUIDValidationError() extends BusinessValidationError {
  override val message: String = "UUID is not valid!"
}

object BusinessValidator extends BusinessValidation {

  def validate[T](
      value: T
  )(onError: => BusinessValidationError)(condition: => Boolean): BusinessValidationResult[T] = {
    if (condition) value.validNel else onError.invalidNel
  }

  def validateTitle(title: String): BusinessValidationResult[String] = {
    validate(title)(TitleValidationError(title)) {
      title.length > 0 && title.length <= 10
    }
  }

  def validateAge(age: Int): BusinessValidationResult[Int] = {
    validate(age)(AgeValidationError(age))(age >= 18)
  }

  def validateVersion(version: Int): BusinessValidationResult[Int] = {
    validate(version)(VersionValidationError(version))(version > 0)
  }

  def validateUuid(uuid: UUID): BusinessValidationResult[UUID] = {
    validate(uuid)(UUIDValidationError()) {
      true
    }
  }

  def validateData(input: Data): BusinessValidationResult[Data] = {
    (
      accept(input.uuid),
      validateTitle(input.title),
      validateAge(input.age),
      validateVersion(input.version)
    ).mapN(Data)
  }
}
