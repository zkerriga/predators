package com.zkerriga.id.storages.players

import sttp.model.StatusCode
import com.zkerriga.id.domain.player.Login
import com.zkerriga.id.endpoints.errors.{EndpointError, EndpointErrorCompanion}

object errors:
  case class LoginConflictError(login: Login) extends EndpointError {
    def description: String = s"This login '$login' is occupied or prohibited to use"
  }

  object LoginConflictError extends EndpointErrorCompanion[LoginConflictError] {
    val statusCode: StatusCode = StatusCode.Conflict
    val textCode: String       = "LOGIN_CONFLICT_ERROR"

    val Example: LoginConflictError = LoginConflictError(Login.Example)
  }
