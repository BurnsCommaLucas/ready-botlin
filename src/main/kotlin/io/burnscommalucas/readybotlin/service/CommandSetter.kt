package io.burnscommalucas.readybotlin.service

import io.burnscommalucas.readybotlin.DiscordConfig
import io.burnscommalucas.readybotlin.model.command.Command
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class CommandSetter(private val discordConfig: DiscordConfig) {
    private val log = LoggerFactory.getLogger(this.javaClass)
    private val client = discordConfig.getDiscordClient()

    @PostConstruct
    fun registerCommands() = runBlocking {
        try {
            client.restClient.applicationService
                .bulkOverwriteGlobalApplicationCommand(
                    discordConfig.getApplicationId(),
                    Command.values().map { it.getApplicationCommandRequest() },
                )
                .collectList()
                .awaitFirstOrNull()
            log.info("Registered slash commands: ${Command.values().map { it.prettyJson() }}")
        } catch (e: Exception) {
            log.error("Failed to update slash commands!", e)
        }
    }
}
