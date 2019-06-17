package com.ikempf

import java.net.URI

import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake

object JavaWebsocketClient extends App {
  val c = new Client(new URI("ws://127.0.0.1:8080/wsecho"))
  c.connect()
}

class Client(uri: URI) extends WebSocketClient(uri) {
  override def onOpen(handshakedata: ServerHandshake): Unit = {
    println("Opened connection")
    send("test")
  }

  override def onMessage(message: String): Unit = {
    println("Received: " + message)
    close(1000)
  }

  override def onClose(code: Int, reason: String, remote: Boolean): Unit = {
    val origin = if (remote) "remote peer" else "us"
    println("Connection closed by " + origin + " Code: " + code + " Reason: " + reason)
  }

  override def onError(ex: Exception): Unit = {
    ex.printStackTrace()
  }
}
