package io.burnscommalucas.readybotlin.service.listener

import discord4j.core.GatewayDiscordClient
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import io.burnscommalucas.readybotlin.model.OutboundMessaging.PLEASE_REPORT
import io.burnscommalucas.readybotlin.model.command.Command
import io.burnscommalucas.readybotlin.model.command.Command.CHECK
import io.burnscommalucas.readybotlin.model.command.Command.DOCS
import io.burnscommalucas.readybotlin.model.command.Command.HELP
import io.burnscommalucas.readybotlin.model.command.Command.READY
import io.burnscommalucas.readybotlin.model.command.Command.UNREADY
import io.burnscommalucas.readybotlin.model.command.Command.WHO
import io.burnscommalucas.readybotlin.service.handler.CheckHandlers
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import javax.annotation.PostConstruct

@Service
class CheckListeners(
    private val discordClient: GatewayDiscordClient,
    private val checkHandlers: CheckHandlers,
) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @PostConstruct
    fun applyListeners() {
        discordClient.on(ChatInputInteractionEvent::class.java) { event ->
            try {
                runBlocking { slashCommandListener(event) }
            } catch (e: Exception) {
                log.error("Failed to respond to '${event.commandName}' command!", e)
                event.editReply("Sorry, something went wrong and I couldn't complete your request. $PLEASE_REPORT")
            }
        }.subscribe()
    }

    suspend fun slashCommandListener(event: ChatInputInteractionEvent): Mono<Void> =
        when (Command.valueOfOrNull(event.commandName)) {
            HELP -> checkHandlers.help(event)
            DOCS -> checkHandlers.docs(event)
            WHO -> checkHandlers.who(event)
            CHECK -> checkHandlers.handleNewCheck(event)
            READY -> checkHandlers.handleReady(event)
            UNREADY -> checkHandlers.unreadyHandler(event)
            null -> {
                log.error("Unrecognized command '${event.commandName}'")
                event.reply("Yikes! Somehow a command I don't recognize made it into my system. $PLEASE_REPORT")
                    .withEphemeral(true)
            }
        }
}