package com.ikempf

import org.asynchttpclient.Dsl._
import org.asynchttpclient.ws.{WebSocket, WebSocketListener, WebSocketUpgradeHandler}

object AsyncHttpClient extends App {

  val c = asyncHttpClient()

  val websocket = c
    .prepareGet("ws://127.0.0.1:8080/wsclose")
    .execute(
      new WebSocketUpgradeHandler.Builder()
        .addWebSocketListener(new WebSocketListener() {

          override def onOpen(websocket: WebSocket) {
            websocket.sendTextFrame("")
          }

          override def onTextFrame(payload: String, finalFragment: Boolean, rsv: Int) {
            println(s"Text frame $payload $finalFragment $rsv")
          }

          override def onError(t: Throwable): Unit = {
            println(s"Error ${t.getMessage}")
          }

          override def onClose(websocket: WebSocket, code: Int, reason: String): Unit = {
            println(s"Closed $code $reason")
            websocket.sendCloseFrame(1000, reason)
          }

        })
        .build())
    .get()

}
