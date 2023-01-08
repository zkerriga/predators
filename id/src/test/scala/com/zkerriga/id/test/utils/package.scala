package com.zkerriga.id.test

import zio.{Tag, ZIO}
import zio.test.*

package object utils:
  // todo: remove later if unused
  def testOnWith[Service: Tag](
    name: String
  ): [E] => (Service => ZIO[Any, E, TestResult]) => Spec[Service, Any] =
    [E] =>
      (f: Service => ZIO[Any, E, TestResult]) =>
        test(name) {
          ZIO.service[Service].flatMap(f)
      }

  def testOn[Service: Tag](
    name: String
  )(f: Service => ZIO[Any, Any, TestResult]): Spec[Service, Any] =
    test(name) {
      ZIO.service[Service].flatMap(f)
    }
