package com.zkerriga.id.endpoints.errors

import com.zkerriga.id.endpoints.errors.EndpointError
import sttp.model.StatusCode
import sttp.tapir.*
import sttp.tapir.json.zio.jsonBody
import zio.json.{JsonDecoder, JsonEncoder}

import scala.reflect.ClassTag

trait EndpointErrorCompanion[E <: EndpointError: ClassTag] {
  val statusCode: StatusCode
  val textCode: String

  val Example: E

  private val typeMatcher: PartialFunction[Any, Boolean] = { case _: E => true }
  private val toStandard: E => ErrorStandard = e => ErrorStandard(textCode, e.description)

  private given JsonEncoder[E] = JsonEncoder[ErrorStandard].contramap[E](toStandard)
  // Errors must only be encoded, so the decoder will not be called
  private given JsonDecoder[E] = JsonDecoder[ErrorStandard].mapOrFail(_ => Left("not implemented"))
  private given Schema[E]      = summon[Schema[ErrorStandard]].map(_ => None)(toStandard)

  given EndpointOutput.OneOfVariant[E] =
    oneOfVariantValueMatcher[E](statusCode, jsonBody[E].example(Example))(typeMatcher)
}
