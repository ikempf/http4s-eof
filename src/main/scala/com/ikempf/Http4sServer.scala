package com.ikempf

import cats.effect._
import cats.implicits._
import com.ikempf.Http4sServer.prt
import fs2._
import fs2.concurrent.Queue
import org.http4s._
import org.http4s.dsl.Http4sDsl
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.websocket._
import org.http4s.websocket.WebSocketFrame
import org.http4s.websocket.WebSocketFrame._

import scala.concurrent.duration._

object Http4sStart extends IOApp {

  override def run(args: List[String]): IO[ExitCode] =
    Http4sServer[IO].stream.compile.drain.as(ExitCode.Success)

}

class Http4sServer[F[_]](implicit F: ConcurrentEffect[F], timer: Timer[F]) extends Http4sDsl[F] {

  def routes: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / "hello" =>
      Ok("Hello world.")

    case GET -> Root / "wsping" =>
      val toClient: Stream[F, WebSocketFrame] =
        Stream.awakeEvery[F](1.seconds).map(d => Text(s"Ping! $d"))
      val fromClient: Pipe[F, WebSocketFrame, Unit] = _.evalMap {
        case Text(msg, _) => prt(s"Received text $msg")
        case frame        => prt(s"Unknown type: $frame")
      }
      WebSocketBuilder[F].build(toClient, fromClient)

    case GET -> Root / "wsecho" =>
      val echoReply: Pipe[F, WebSocketFrame, WebSocketFrame] =
        _.evalMap {
          case Text(msg, _) =>
            prt(s"Received text $msg")
              .as(Text(s"You sent the server: $msg"))
          case a =>
            prt(s"Received else $a")
              .as(Text("Something new"))
        }

      Queue
        .unbounded[F, WebSocketFrame]
        .flatMap { q =>
          val d = q.dequeue.through(echoReply)
          val e = q.enqueue
          WebSocketBuilder[F].build(d, e)
        }

    case GET -> Root / "wsclose" =>
      val echoReply: Pipe[F, WebSocketFrame, WebSocketFrame] =
        _.evalMap { frame =>
          prt(s"Received $frame")
            .as(Close(1000).right.get)
        }

      Queue
        .unbounded[F, WebSocketFrame]
        .flatMap { q =>
          val d = q.dequeue.through(echoReply)
          val e = q.enqueue
          WebSocketBuilder[F].build(d, e)
        }
  }

  def stream: Stream[F, ExitCode] =
    BlazeServerBuilder[F]
      .bindHttp(8080)
      .withWebSockets(true)
      .withHttpApp(routes.orNotFound)
      .serve
}

object Http4sServer {

  def apply[F[_]: ConcurrentEffect: Timer]: Http4sServer[F] =
    new Http4sServer[F]

  def prt[F[_]: Sync](s: String): F[Unit] =
    Sync[F].delay(println(s))

}
