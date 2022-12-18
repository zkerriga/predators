package com.zkerriga.id

import sttp.model.Method
import zio.logging.LogFormat.*
import zio.logging.backend.SLF4J
import zio.logging.{LogAnnotation, LogFilter, LogFormat, consoleJson}
import zio.{LogLevel, Runtime, ZIOAspect, ZLayer}

import java.time.format.DateTimeFormatter

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

  /* todo: think about using slf4j for structural logging */
  val bootstrapLayer: ZLayer[Any, Nothing, Unit] =
    Runtime.removeDefaultLoggers >>> consoleJson(
      label("timestamp", timestamp(DateTimeFormatter.ISO_LOCAL_DATE_TIME)) +
        label("level", level) +
        label("thread", fiberId) +
        label("message", line) +
        (space + label("cause", cause)).filter(LogFilter.causeNonEmpty) +
        annotation(LogAnnotation.TraceId) +
        annotation(operationAnnotation) +
        spans,
      LogLevel.Debug,
    )
