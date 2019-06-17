package com.ikempf

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws.{Message, TextMessage, WebSocketRequest}
import akka.stream.scaladsl.{Keep, Sink, Source}
import akka.stream.{ActorMaterializer, OverflowStrategy}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Milliseconds, Seconds, Span}

object AkkaClient extends App with ScalaFutures {
  implicit val patience: PatienceConfig        = PatienceConfig(Span(5, Seconds), Span(100, Milliseconds))
  implicit val system: ActorSystem             = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()

//    val WsUrl   = "ws://localhost:8080/wsping"
//  val WsUrl   = "ws://localhost:8080/wsecho"
    val WsUrl   = "ws://localhost:8080/wsclose"
  val request = WebSocketRequest(WsUrl)

  val sourceQueue = Source.queue[Message](1000, OverflowStrategy.fail)
  val flow        = Http().webSocketClientFlow(request)
  val sinkQueue   = Sink.queue[Message]()

  val (input, output) =
    sourceQueue
      .via(flow)
      .toMat(sinkQueue)(Keep.both)
      .run()

  Range(0, 2).foreach { _ =>
    input.offer(TextMessage("")).futureValue
//    println(("Output", output.pull().futureValue))
  }

  input.complete()

}
