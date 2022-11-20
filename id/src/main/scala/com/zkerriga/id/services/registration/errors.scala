package com.zkerriga.id.services.registration

import com.zkerriga.id.domain.player.Login
import com.zkerriga.id.endpoints.errors.{EndpointError, EndpointErrorCompanion}
import sttp.model.StatusCode

object errors:
  case class LoginConflictError(login: Login) extends EndpointError {
    def description: String = s"This login '$login' is occupied or prohibited to use"
  }
  object LoginConflictError extends EndpointErrorCompanion[LoginConflictError] {
    val statusCode: StatusCode = StatusCode.Conflict
    val textCode: String       = "LOGIN_CONFLICT_ERROR"

    val Example: LoginConflictError = LoginConflictError(Login.Example)
  }
