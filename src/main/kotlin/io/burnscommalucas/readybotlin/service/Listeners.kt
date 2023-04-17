package io.burnscommalucas.readybotlin.service

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import io.burnscommalucas.readybotlin.DiscordConfig
import io.burnscommalucas.readybotlin.model.command.Command
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import javax.annotation.PostConstruct

@Component
class Listeners(
    discordConfig: DiscordConfig,
    val handlers: Handlers,
) {
    private val log = LoggerFactory.getLogger(this.javaClass)
    private val client = discordConfig.getDiscordClient()

    @PostConstruct
    fun applyListeners() {
        client.on(ChatInputInteractionEvent::class.java) { event ->
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
    }

    suspend fun slashCommandListener(event: ChatInputInteractionEvent): Mono<Void> =
        when (Command.valueOfOrNull(event.commandName)) {
            Command.HELP -> handlers.help(event)
            Command.DOCS -> handlers.docs(event)
            Command.WHO -> handlers.who(event)
            Command.CHECK -> handlers.handleNewCheck(event)
            Command.READY -> handlers.handleReady(event)
            Command.UNREADY -> handlers.unreadyHandler(event)
            null -> {
                log.info("Unrecognized command '${event.commandName}'")
                Mono.empty()
            }
        }
}