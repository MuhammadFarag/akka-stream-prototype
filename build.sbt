name := "akka-stream-prototype"

version := "0.1"

scalaVersion := "2.12.4"

val akkaVersion = "2.5.9"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % akkaVersion
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % akkaVersion
libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.4" % Test