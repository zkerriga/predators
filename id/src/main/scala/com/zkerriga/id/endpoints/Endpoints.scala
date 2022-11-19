package com.zkerriga.id.endpoints

import com.zkerriga.id.services.registration.RegistrationService
import sttp.capabilities.zio.ZioStreams
import sttp.tapir.AnyEndpoint
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.server.metrics.prometheus.PrometheusMetrics
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import sttp.tapir.ztapir.ZServerEndpoint
import zio.{RIO, Task}

object Endpoints:
  private val apiEndpoints: List[AnyEndpoint] = List(
    PlayerRegistration.protocol
  )
  private val docEndpoints: List[ZServerEndpoint[Any, Any]] =
    SwaggerInterpreter().fromEndpoints[Task](apiEndpoints, "id", "1.0.0")

  private val apiServerEndpoints: List[ZServerEndpoint[RegistrationService, Any]] = List(
    PlayerRegistration.logic
  )

  final val all =
    (apiServerEndpoints ++ docEndpoints)
      .asInstanceOf[List[ZServerEndpoint[RegistrationService, ZioStreams]]]
