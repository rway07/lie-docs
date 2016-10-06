name := """dGogle"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  "com.typesafe.akka" %% "akka-cluster" % "2.4.11",
  "com.typesafe.akka" %% "akka-cluster-tools" % "2.4.11",
  "mysql" % "mysql-connector-java" % "5.1.36"
)

