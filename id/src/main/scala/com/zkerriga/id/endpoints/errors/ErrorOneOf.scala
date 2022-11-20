package com.zkerriga.id.endpoints.errors

import sttp.tapir.EndpointOutput.{OneOfVariant => Variant, OneOf}
import sttp.tapir.oneOf

object ErrorOneOf {
  def genUnion[A1, A2](using
    v1: Variant[A1],
    v2: Variant[A2],
  ): OneOf[A1 | A2, A1 | A2] = oneOf[A1 | A2](v1, v2)

  def genUnion[A1, A2, A3](using
    v1: Variant[A1],
    v2: Variant[A2],
    v3: Variant[A3],
  ): OneOf[A1 | A2 | A3, A1 | A2 | A3] = oneOf[A1 | A2 | A3](v1, v2, v3)

  def genUnion[A1, A2, A3, A4](using
    v1: Variant[A1],
    v2: Variant[A2],
    v3: Variant[A3],
    v4: Variant[A4],
  ): OneOf[A1 | A2 | A3 | A4, A1 | A2 | A3 | A4] = oneOf[A1 | A2 | A3 | A4](v1, v2, v3, v4)
}
