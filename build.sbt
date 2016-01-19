

name := "ReactiveFizzBuzz"

version := "0.1"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "com.typesafe.akka" % "akka-stream-experimental_2.11" % "2.0.2",
  "com.typesafe.akka" % "akka-http-core-experimental_2.11" % "2.0.2",
  "com.typesafe.akka" % "akka-http-experimental_2.11" % "2.0.2" 
)

fork in run := true
