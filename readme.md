## Unexpected EOF

When the server side closes the socket (cleanly) the following e
```
http4s-eof 15:26:15.903 [scala-execution-context-global-38] ERROR org.http4s.blazecore.websocket.Http4sWSStage - Error closing Web Socket
http4s-eof org.http4s.blaze.pipeline.Command$EOF$: EOF
http4s-eof 	at org.http4s.blaze.pipeline.Command$EOF$.scala$util$control$NoStackTrace$$super$fillInStackTrace(Command.scala:23)
http4s-eof 	at scala.util.control.NoStackTrace.fillInStackTrace(NoStackTrace.scala:28)
http4s-eof 	at scala.util.control.NoStackTrace.fillInStackTrace$(NoStackTrace.scala:27)
http4s-eof 	at org.http4s.blaze.pipeline.Command$EOF$.fillInStackTrace(Command.scala:23)
http4s-eof 	at java.lang.Throwable.<init>(Throwable.java:265)
http4s-eof 	at java.lang.Exception.<init>(Exception.java:66)
http4s-eof 	at org.http4s.blaze.pipeline.Command$EOF$.<init>(Command.scala:23)
http4s-eof 	at org.http4s.blaze.pipeline.Command$EOF$.<clinit>(Command.scala)
http4s-eof 	at org.http4s.blaze.channel.nio1.NIO1HeadStage$$anon$3.run(NIO1HeadStage.scala:322)
http4s-eof 	at org.http4s.blaze.util.TaskQueue.go$1(TaskQueue.scala:82)
http4s-eof 	at org.http4s.blaze.util.TaskQueue.executeTasks(TaskQueue.scala:99)
http4s-eof 	at org.http4s.blaze.channel.nio1.SelectorLoop.org$http4s$blaze$channel$nio1$SelectorLoop$$runLoop(SelectorLoop.scala:166)
http4s-eof 	at org.http4s.blaze.channel.nio1.SelectorLoop$$anon$1.run(SelectorLoop.scala:67)
http4s-eof 	at java.lang.Thread.run(Thread.java:748)
```
