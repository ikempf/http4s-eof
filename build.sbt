val http4sVersion = "0.20.0-M6"

lazy val `http4s-eof` = (project in file("."))
  .settings(
    organization := "com.ikempf",
    name := "http4s-eof",
    scalaVersion := "2.12.8",
    scalacOptions += "-Ypartial-unification",
    libraryDependencies ++= List(
      "org.typelevel"       %% "cats-core"           % "1.6.0",
      "io.chrisdavenport"   %% "log4cats-slf4j"      % "0.3.0",
      "ch.qos.logback"      % "logback-classic"      % "1.2.3",
      "org.http4s"          %% "http4s-dsl"          % http4sVersion,
      "org.http4s"          %% "http4s-blaze-server" % http4sVersion,
      "org.scalatest"       %% "scalatest"           % "3.0.5" % Test,
      "com.typesafe.akka"   %% "akka-http"           % "10.1.5" % Test,
      "org.asynchttpclient" % "async-http-client"    % "2.10.0" % Test,
      "com.typesafe.akka"   %% "akka-stream"         % "2.5.19" % Test,
    ),
    addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.8"),
    scalacOptions ++= List(
      "-target:jvm-1.8",
      "-feature",
      "-encoding",
      "UTF-8",
      "-unchecked",
      "-deprecation",
      "-language:higherKinds",
      "-Xlint",
      "-Ypartial-unification",
      "-Ywarn-dead-code",
      "-Ywarn-infer-any",
      "-Ywarn-nullary-override",
      "-Ywarn-unused:implicits",
      "-Ywarn-unused:imports",
      "-Ywarn-unused:locals",
      "-Ywarn-unused:params",
      "-Ywarn-unused:patvars",
      "-Ywarn-unused:privates",
      "-Ywarn-value-discard"
    ),
  )
