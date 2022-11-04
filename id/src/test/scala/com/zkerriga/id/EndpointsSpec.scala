package com.zkerriga.id

import com.zkerriga.id.Endpoints.{booksListingServerEndpoint, helloServerEndpoint}
import com.zkerriga.id.Library.{Book, books}
import sttp.client3.testing.SttpBackendStub
import sttp.client3.ziojson.asJson
import sttp.tapir.server.stub.TapirStubInterpreter
import sttp.tapir.ztapir.RIOMonadError
import zio.test.Assertion.{equalTo, isRight}
import zio.test.ZIOSpecDefault

object EndpointsSpec extends ZIOSpecDefault:
  def spec = suite("Endpoints spec")(
    test("return hello message") {
      // given
      val backendStub = TapirStubInterpreter(SttpBackendStub(new RIOMonadError[Any]))
        .whenServerEndpoint(helloServerEndpoint)
        .thenRunLogic()
        .backend()

      // when
      val response = basicRequest
        .get(uri"http://test.com/hello?name=adam")
        .send(backendStub)

      // then
      assertZIO(response.map(_.body))(isRight(equalTo("Hello adam")))
    },
    test("list available books") {
      // given
      val backendStub = TapirStubInterpreter(SttpBackendStub(new RIOMonadError[Any]))
        .whenServerEndpoint(booksListingServerEndpoint)
        .thenRunLogic()
        .backend()

      // when
      val response = basicRequest
        .get(uri"http://test.com/books/list/all")
        .response(asJson[List[Book]])
        .send(backendStub)

      // then
      assertZIO(response.map(_.body))(isRight(equalTo(books)))
    },
  )
