package com.zkerriga.id.endpoints.errors

import sttp.model.StatusCode

object InternalError extends EndpointError with EndpointErrorCompanion[InternalError] {
  val statusCode: StatusCode = StatusCode.InternalServerError
  val textCode: String       = "INTERNAL_ERROR"
  val description: String    = "Internal server error"

  val Example: InternalError = InternalError
}
