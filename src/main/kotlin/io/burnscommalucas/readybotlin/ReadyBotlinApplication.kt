package io.burnscommalucas.readybotlin

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ReadyBotlinApplication

fun main(args: Array<String>) {
    runApplication<ReadyBotlinApplication>(*args)
}

//@Component
//class BotConnection(botConfig: BotConfig) :
//	DiscordApi by DiscordApiBuilder().setToken(botConfig.token).login().join()