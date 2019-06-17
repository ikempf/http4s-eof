package com.ikempf

import java.net.InetSocketAddress

import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer

object JavaWebsocketServer extends App {
  val addr = new InetSocketAddress("127.0.0.1", 8080)
  val c = new JavaServer(addr)
  c.start()
}

class JavaServer(addr: InetSocketAddress) extends WebSocketServer(addr) {
  override def onOpen(conn: WebSocket, handshake: ClientHandshake): Unit = {
    println(conn.getRemoteSocketAddress.getAddress.getHostAddress + " entered the room!")
  }

  override def onClose(conn: WebSocket, code: Int, reason: String, remote: Boolean): Unit = {
    println(conn + " has left the room!")
  }

  override def onMessage(conn: WebSocket, message: String): Unit = {
    println("Received " + conn + ": " + message)
    conn.close(1000)
  }

  override def onError(conn: WebSocket, ex: Exception): Unit = {
    ex.printStackTrace()
  }

  override def onStart(): Unit = {
    println("Server started")
  }
}
