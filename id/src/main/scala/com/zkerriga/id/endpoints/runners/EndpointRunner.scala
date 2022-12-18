package com.zkerriga.id.endpoints.runners

import com.zkerriga.id.LoggingSetup.syntax.*
import com.zkerriga.id.endpoints.errors.{EndpointError, InternalError}
import sttp.model.Method
import zio.logging.LogAnnotation
import zio.{IO, Tag, ULayer, URIO, ZIO, ZLayer}

import java.security.Provider.Service
import java.util.UUID

trait EndpointRunner:
  def run[Service: Tag, E <: EndpointError, A](
    call: Service => IO[E, A]
  )(using meta: EndpointMeta): URIO[Service & ErrorHandler, Either[E | InternalError, A]]

object EndpointRunner:
  object Simple extends EndpointRunner {
    def run[Service: Tag, E <: EndpointError, A](
      call: Service => IO[E, A]
    )(using meta: EndpointMeta): URIO[Service & ErrorHandler, Either[E | InternalError, A]] =
      for
        service <- ZIO.service[Service]
        handler <- ZIO.service[ErrorHandler]

        traceId <- ZIO.succeed { UUID.randomUUID() }

        // todo: probably some metrics processing here

        result <- ZIO.logSpan(meta.spanName) {
          ZIO.logInfo("has been started") *>
            handler.handle(call(service)) <*
            ZIO.logInfo("has been finished")
        } @@ LogAnnotation.TraceId(traceId) @@ LogAnnotation.Operation(meta.method, meta.path)
      yield result
  }

  private type Call[Service, E, A] = Service => IO[E, A]

  def on[Service: Tag](using
    EndpointMeta
  ): [E <: EndpointError, A] => Call[Service, E, A] => URIO[Service & ErrorHandler & EndpointRunner, Either[E | InternalError, A]] =
    [E1 <: EndpointError, A1] =>
      (call: Service => IO[E1, A1]) => ZIO.service[EndpointRunner].flatMap { _.run(call) }

  val live: ULayer[EndpointRunner] =
    ZLayer.succeed(Simple)
