package com.ikempf

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import fs2.Stream
import fs2.concurrent.Queue
import org.http4s.dsl.Http4sDsl
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.websocket.WebSocketBuilder
import org.http4s.websocket.WebSocketFrame
import org.http4s.websocket.WebSocketFrame.Text
import org.http4s.{HttpApp, HttpRoutes}

object Main extends IOApp with Http4sDsl[IO] {

  override def run(args: List[String]): IO[ExitCode] =
    BlazeServerBuilder[IO]
      .bindHttp()
      .withHttpApp(httpApp)
      .serve
      .compile[IO, IO, ExitCode]
      .drain
      .as(ExitCode.Success)

  private def httpApp: HttpApp[IO] =
    routes.orNotFound

  private def routes: HttpRoutes[IO] =
    Router("/" -> service)

  private def service: HttpRoutes[IO] =
    HttpRoutes.of {
      case GET -> Root / "open-ws" =>
        Queue
          .unbounded[IO, WebSocketFrame]
          .flatMap(queue =>
            WebSocketBuilder[IO].build(send = in(queue.dequeue), receive = queue.enqueue)
          )
    }

  def in(input: Stream[IO, WebSocketFrame]): Stream[IO, WebSocketFrame] =
    input
      .map {
        case Text(value, _) => Text(show"Received $value")
        case _              => Text("unsupported frame")
      }

}
