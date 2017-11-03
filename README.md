# filmguidebot

A Slack bot that tells you what films are on UK TV over  the next 7 days, and what their IMDB rating is

I followed the tutorial [here](https://blog.scalac.io/2015/07/16/slack.html) to make the bot.

## Configuration

You need to provide an API key from Slack after creating a new Bot (see the [tutorial](https://blog.scalac.io/2015/07/16/slack.html)). Once you have this, create the following file: `src/main/resources/application.conf` with contents:

```
api {
  key = "XXXX"

  base.url = "https://slack.com/api/"
}
websocket.key = "M8eMPBM=7G(igdN-pP.FN0nK"
```
replacing `XXXX` with your key.

## Run:

Simply run with:

```
sbt clean run
```