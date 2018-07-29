package com.rlich.json.utils

import java.util.UUID

trait UuidValidation {

  private val uuidRegex = "(?i:^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$)"

  def validateUuid(string: String): Boolean =
    string.matches(uuidRegex)
}

object UUIDParser extends UuidValidation {

  def parseString(string: String): UUID = {
    if (!validateUuid(string)) throw new IllegalArgumentException("Invalid UUID string: " + string)
    UUID.fromString(string)
  }
}
