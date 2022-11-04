package com.zkerriga.id

import com.zkerriga.id.Library
import com.zkerriga.id.Library.*
import sttp.tapir.*
import sttp.tapir.Codec.JsonCodec
import sttp.tapir.generic.auto.*
import sttp.tapir.json.zio.*
import sttp.tapir.server.metrics.prometheus.PrometheusMetrics
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import sttp.tapir.ztapir.ZServerEndpoint
import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder}
import zio.{Task, ZIO}

object Endpoints:
  case class User(name: String) extends AnyVal
  val helloEndpoint: PublicEndpoint[User, Unit, String, Any] = endpoint.get
    .in("hello")
    .in(query[User]("name"))
    .out(stringBody)
  val helloServerEndpoint: ZServerEndpoint[Any, Any] = helloEndpoint.serverLogicSuccess(user => ZIO.succeed(s"Hello ${user.name}"))

  given authorZioEncoder: zio.json.JsonEncoder[Author] = DeriveJsonEncoder.gen[Author]
  given authorZioDecoder: zio.json.JsonDecoder[Author] = DeriveJsonDecoder.gen[Author]
  given bookZioEncoder: zio.json.JsonEncoder[Book] = DeriveJsonEncoder.gen[Book]
  given bookZioDecoder: zio.json.JsonDecoder[Book] = DeriveJsonDecoder.gen[Book]
  val booksListing: PublicEndpoint[Unit, Unit, List[Book], Any] = endpoint.get
    .in("books" / "list" / "all")
    .out(jsonBody[List[Book]])
  val booksListingServerEndpoint: ZServerEndpoint[Any, Any] = booksListing.serverLogicSuccess(_ => ZIO.succeed(Library.books))

  val apiEndpoints: List[ZServerEndpoint[Any, Any]] = List(helloServerEndpoint, booksListingServerEndpoint)

  val docEndpoints: List[ZServerEndpoint[Any, Any]] = SwaggerInterpreter()
    .fromServerEndpoints[Task](apiEndpoints, "id", "1.0.0")

  val prometheusMetrics: PrometheusMetrics[Task] = PrometheusMetrics.default[Task]()
  val metricsEndpoint: ZServerEndpoint[Any, Any] = prometheusMetrics.metricsEndpoint

  val all: List[ZServerEndpoint[Any, Any]] = apiEndpoints ++ docEndpoints ++ List(metricsEndpoint)

object Library:
  case class Author(name: String)
  case class Book(title: String, year: Int, author: Author)

  val books = List(
    Book("The Sorrows of Young Werther", 1774, Author("Johann Wolfgang von Goethe")),
    Book("On the Niemen", 1888, Author("Eliza Orzeszkowa")),
    Book("The Art of Computer Programming", 1968, Author("Donald Knuth")),
    Book("Pharaoh", 1897, Author("Boleslaw Prus"))
  )
