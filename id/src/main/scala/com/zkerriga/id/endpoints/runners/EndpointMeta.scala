package com.zkerriga.id.endpoints.runners

import sttp.model.Method
import sttp.tapir.AnyEndpoint

case class EndpointMeta(
  method: Method,
  path: String,
  name: String,
) {
  val spanName: String = name.toLowerCase().replace(' ', '-')
}

object EndpointMeta:
  def from(endpoint: AnyEndpoint): EndpointMeta =
    EndpointMeta(
      method = endpoint.method getOrElse {
        throw IllegalStateException(
          s"All endpoints must have specified method! Please, check configuration of ${endpoint.showShort}"
        )
      },
      path = endpoint.showPathTemplate(),
      name = endpoint.info.name getOrElse {
        throw IllegalStateException(
          s"All endpoints must have specified name! Please, check configuration of ${endpoint.showShort}"
        )
      },
    )
