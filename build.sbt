name := "filmguidebot"

version := "1.0"

scalaVersion := "2.11.1"


// A library to help build a bot. See: https://blog.scalac.io/2015/07/16/slack.html
// (note that scala version above must be 2.11.1 otherwise it fails)

resolvers += "scalac repo" at "https://raw.githubusercontent.com/ScalaConsultants/mvn-repo/master/"

libraryDependencies ++= Seq("io.scalac" %% "slack-scala-bot-core" % "0.2.1")