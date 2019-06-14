package com.ikempf

import cats.effect._
import cats.implicits._
import com.ikempf.Server.prt
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

object Start extends IOApp {

  override def run(args: List[String]): IO[ExitCode] =
    Server[IO].stream.compile.drain.as(ExitCode.Success)

}

class Server[F[_]](implicit F: ConcurrentEffect[F], timer: Timer[F]) extends Http4sDsl[F] {

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
        _.collect {
          case Text(msg, _) =>
            prt(s"Received text $msg")
            Text(s"You sent the server: $msg")
          case a =>
            prt(s"Received else $a")
            Text("Something new")
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
        _.collect {
          case a =>
            prt(s"Received $a")
            Close(1000).right.get
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

object Server {

  def apply[F[_]: ConcurrentEffect: Timer]: Server[F] =
    new Server[F]

  def prt[F[_]: Sync](s: String): F[Unit] =
    Sync[F].delay(println(s))

}
