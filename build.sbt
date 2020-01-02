organization := "hello"
name := "akka-cluster"
version := "1.0"

scalaVersion := "2.13.1"

val akkaVersion = "2.6.0"
val akkaManagementVersion = "1.0.5"
val fstVersion = "2.56"

libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-sharding" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-metrics" % akkaVersion,
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "com.lightbend.akka.management" %% "akka-management" % akkaManagementVersion,
  "com.lightbend.akka.management" %% "akka-management-cluster-http" % akkaManagementVersion,
  "de.ruedigermoeller" % "fst" % fstVersion
)

assemblyJarName in assembly := "akka-cluster.jar"
test in assembly := {}
