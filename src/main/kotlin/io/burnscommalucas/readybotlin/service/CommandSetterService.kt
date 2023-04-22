package io.burnscommalucas.readybotlin.service

import io.burnscommalucas.readybotlin.configuration.DiscordConfig
import io.burnscommalucas.readybotlin.model.command.Command
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

/**
 * This service registers all slash commands with discord on boot.
 */
@Service
class CommandSetterService(private val discordConfig: DiscordConfig) {
    private val log = LoggerFactory.getLogger(this.javaClass)
    private val client = discordConfig.getDiscordClient()
    private val scope = CoroutineScope(Dispatchers.Default)

    @PostConstruct
    fun registerCommands() = scope.launch {
        try {
            client.restClient.applicationService
                .bulkOverwriteGlobalApplicationCommand(
                    client.restClient.applicationId.awaitFirstOrNull()
                        ?: throw IllegalStateException("Failed to retrieve ApplicationID from rest client"),
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
