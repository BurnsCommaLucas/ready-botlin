package io.burnscommalucas.readybotlin.service.listener

import discord4j.core.GatewayDiscordClient
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.core.event.domain.lifecycle.ConnectEvent
import discord4j.core.event.domain.lifecycle.DisconnectEvent
import io.burnscommalucas.readybotlin.model.command.Command
import io.burnscommalucas.readybotlin.service.handler.CheckHandlers
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import javax.annotation.PostConstruct

@Component
class CheckListeners(
    private val discordClient: GatewayDiscordClient,
    private val checkHandlers: CheckHandlers,
) {
    private val log = LoggerFactory.getLogger(this.javaClass)
    private var clientIsConnected = false

    @PostConstruct
    fun applyListeners() {
        discordClient.on(ChatInputInteractionEvent::class.java) { event ->
            try {
                runBlocking { slashCommandListener(event) }
            } catch (e: Exception) {
                log.error("Failed to respond to '${event.commandName}' command!", e)
                event.editReply(
                    """
                    Sorry, something went wrong and I couldn't create your check.
                    
                    Please type `/${Command.DOCS}` to report this to my maker!
                    """.trimIndent()
                )
            }
        }.subscribe()

        discordClient.on(ConnectEvent::class.java) {
            try {
                clientIsConnected = true
                Mono.empty<Void>()
            } catch (e: Exception) {
                log.error("Error while monitoring connect events!", e)
                Mono.empty()
            }
        }.subscribe()

        discordClient.on(DisconnectEvent::class.java) {
            try {
                clientIsConnected = false
                Mono.empty<Void>()
            } catch (e: Exception) {
                log.error("Error while monitoring connect events!", e)
                Mono.empty()
            }
        }.subscribe()
    }

    suspend fun slashCommandListener(event: ChatInputInteractionEvent): Mono<Void> =
        when (Command.valueOfOrNull(event.commandName)) {
            Command.HELP -> checkHandlers.help(event)
            Command.DOCS -> checkHandlers.docs(event)
            Command.WHO -> checkHandlers.who(event)
            Command.CHECK -> checkHandlers.handleNewCheck(event)
            Command.READY -> checkHandlers.handleReady(event)
            Command.UNREADY -> checkHandlers.unreadyHandler(event)
            null -> {
                log.info("Unrecognized command '${event.commandName}'")
                Mono.empty()
            }
        }
}