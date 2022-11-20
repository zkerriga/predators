package com.zkerriga.id.endpoints.errors

import sttp.tapir.Schema
import zio.json.{DeriveJsonCodec, JsonCodec}

private[errors] case class ErrorStandard(code: String, description: String)

private[errors] object ErrorStandard:
  given JsonCodec[ErrorStandard] = DeriveJsonCodec.gen[ErrorStandard]
  given Schema[ErrorStandard]    = Schema.derived[ErrorStandard]
