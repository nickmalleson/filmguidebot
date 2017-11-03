import akka.actor.{ActorContext, ActorRef, ActorSystem, Props}
import io.scalac.slack.api.{BotInfo, Start}
import io.scalac.slack.bots.system.{CommandsRecognizerBot, HelpBot}
import io.scalac.slack.common.actors.SlackBotActor
import io.scalac.slack.common.{Shutdownable, UsersStorage}
import io.scalac.slack.example.CalculatorBot
import io.scalac.slack.{BotModules, MessageEventBus, Config => SlackConfig}
import io.scalac.slack.websockets.{WSActor, WebSocket}


/* Source:
https://github.com/pjazdzewski1990/scala-slack-bot-example/blob/master/src/main/scala/io/scalac/slack/example/BotRunner.scala
 */

object BotRunner extends Shutdownable {
  val system = ActorSystem("SlackBotSystem") // The Slack Bot is written on top of Akka
  val eventBus = new MessageEventBus

  // The SlackBotActor is the main class used. It takes a few parameters:
  val slackBot = system.actorOf(
    Props(
      classOf[SlackBotActor],
      new ExampleBotsBundle(), // BotModules instance
      eventBus, // MessageEventsBus (for communication)
      this, // A reference to the master class
      None // An (optional?) ActorRef pointing to UserStorage actor
    ),
    "slack-bot")

  var botInfo: Option[BotInfo] = None

  def main(args: Array[String]) {
    println("SlackBot started")
    println("With api key: " + SlackConfig.apiKey)

    try {
      slackBot ! Start

      system.awaitTermination()
      println("Shutdown successful...")
    } catch {
      case e: Exception =>
        println("An unhandled exception occurred...", e)
        system.shutdown()
        system.awaitTermination()
    }
  }

  sys.addShutdownHook(shutdown())

  override def shutdown(): Unit = {
    slackBot ! WebSocket.Release
    system.shutdown()
    system.awaitTermination()
  }

  /**
    * The BotModules bundle allows you to add multiple bot instances in one bundle
    * using the registerModules function.
    *
    * (For now start three example bots: CommandsRecognizerBot, HelpBot and CalculatorBot
    * (a bot that we will define - see the <code>CalculatorBot</code> class).
    */
  class ExampleBotsBundle() extends BotModules {
    override def registerModules(context: ActorContext, websocketClient: ActorRef) = {
      context.actorOf(Props(classOf[CommandsRecognizerBot], eventBus), "commandProcessor")
      context.actorOf(Props(classOf[HelpBot], eventBus), "helpBot")
      context.actorOf(Props(classOf[CalculatorBot], eventBus), "CalculatorBot")
    }
  }
}