package com.zkerriga.id

import sttp.model.Method
import zio.logging.{LogAnnotation, LogFormat, consoleJson}
import zio.{Runtime, ZIOAspect, ZLayer}

object LoggingSetup:
  private val operationAnnotation: LogAnnotation[(Method, String)] =
    LogAnnotation[(Method, String)](
      "operation",
      (_, last) => last,
      { case (method, path) => s"$method $path" },
    )

  object syntax {
    extension (o: LogAnnotation.type) {
      def Operation(method: Method, path: String) =
        operationAnnotation(method -> path)
    }
  }

  val bootstrapLayer: ZLayer[Any, Nothing, Unit] =
    Runtime.removeDefaultLoggers >>> consoleJson(
      LogFormat.default +
        LogFormat.annotation(LogAnnotation.TraceId) +
        LogFormat.annotation(operationAnnotation) +
        LogFormat.spans
    )
