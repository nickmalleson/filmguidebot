/* Source: https://github.com/pjazdzewski1990/scala-slack-bot-example/blob/master/src/main/scala/io/scalac/slack/example/CalculatorBot.scala */
package io.scalac.slack.example

import io.scalac.slack.MessageEventBus
import io.scalac.slack.bots.AbstractBot
import io.scalac.slack.common.{BaseMessage, Command, OutboundMessage}

/**
  * Extends AbstractBot, which is a thin wrapper around Akka’s Actor class
  * providing few useful utility methods for bot authors and constraints a
  * Bot should meet.
  * @param bus For communication
  */
class CalculatorBot(override val bus: MessageEventBus) extends AbstractBot {

  /**
    * Return a help message.
    * @param channel
    * @return
    */
  override def help(channel: String): OutboundMessage =
    OutboundMessage(channel,
      s"$name will help you to solve difficult math problems \\n" +
        "Usage: $calc {operation} {arguments separated by space}")

  val possibleOperations = Map(
    "+" -> ((x: Double, y: Double) => x+y),
    "-" -> ((x: Double, y: Double) => x-y),
    "*" -> ((x: Double, y: Double) => x*y),
    "/" -> ((x: Double, y: Double) => x/y)
  )

  /**
    * How to behave when a message is received (?).
    *
    * Match on <code>Command</code> instances to determin what action to take.
    * <code>Command</code> classes have a leading 'command', a list of arguments,
    * and the raw message (which includes,e.g., the channel ID).
    * @return
    */
  override def act: Receive = {

    // First case is a correctly specified command with >0 arguments
    case Command("calc", operation :: args, message) if args.length >= 1 =>

      val op = possibleOperations.get(operation)
      val response = op.map(f => {
        val result = args.map(_.toDouble).reduceLeft( f(_,_) )
        OutboundMessage(message.channel, s"Results is: $result")
      }).getOrElse(OutboundMessage(message.channel, s"No operation $operation"))

      publish(response)

    // This case indicates that no arguments have been specified:
    case Command("calc", _, message) =>
      publish(OutboundMessage(message.channel, s"No arguments specified!"))
  }
}
