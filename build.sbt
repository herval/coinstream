name := "coinstream"

version := "1.0"

scalaVersion := "2.11.11"

mainClass in assembly := Some("hervalicious.coinstream.Monitor")

assemblyOutputPath in assembly := file("./coinstream.jar")

libraryDependencies ~= { _ map {
  case m if m.organization == "org.knowm.xchange" =>
    m.exclude("com.google.code.findbugs", "jsr305")
  case m => m
}}

resolvers ++= Seq(
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.4.2",
  "io.getquill" %% "quill-jdbc" % "1.2.1",
  "org.postgresql" % "postgresql" % "9.4.1208",
  ("org.knowm.xchange" % "xchange-core" % "4.2.0").exclude("com.google.code.findbugs", "jsr305"),
  "org.knowm.xchange" % "xchange-examples" % "4.2.0"
)

