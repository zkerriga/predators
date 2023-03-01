package com.zkerriga.id

import com.zkerriga.id.endpoints.PlayerRegistration.RegistrationData
import zio.*
import zio.json.{JsonCodec, JsonDecoder, JsonEncoder}
import zio.kafka.producer.*
import zio.kafka.serde.Serializer

object TestDomain {
  opaque type MyId = Long
  object MyId {
    val Test: MyId = 1212L
  }

  opaque type GameScope = String
  object GameScope {
    val Test: GameScope = "kekes"
  }

  case class Data(id: MyId, scopes: Set[GameScope])
  object Data {
    given encoder: JsonEncoder[Data] = zio.json.DeriveJsonEncoder.gen
    given decoder: JsonDecoder[Data] = zio.json.DeriveJsonDecoder.gen

    val serializer: Serializer[Any, Data] =
      Serializer.string.inmap[Data](s => decoder.decodeJson(s).getOrElse(???))(data =>
        encoder.encodeJson(data).toString
      )
  }
}

object TestKafka extends ZIOAppDefault {
  import TestDomain.*

  val producerSettings: ProducerSettings = ProducerSettings(List("localhost:29092"))

  val producer: ZLayer[Any, Throwable, Producer] = ZLayer.scoped(Producer.make(producerSettings))

  def notifyAboutData(data: Data): ZIO[Producer, Throwable, Unit] =
    for
      producer <- ZIO.service[Producer]
      meta <- producer.produce(
        topic = "local-data-topic",
        key = "some-key",
        value = data,
        keySerializer = Serializer.string,
        valueSerializer = Data.serializer,
      )
      _ <- ZIO.logInfo(meta.toString)
    yield ()

  val run = notifyAboutData(Data(MyId.Test, Set.empty)).provide(producer)
}
