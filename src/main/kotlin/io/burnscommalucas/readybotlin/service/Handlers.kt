package io.burnscommalucas.readybotlin.service

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import io.burnscommalucas.readybotlin.database.CheckRepository
import io.burnscommalucas.readybotlin.plural
import io.burnscommalucas.readybotlin.service.OutboundMessaging.DOCS_MESSAGE
import io.burnscommalucas.readybotlin.service.OutboundMessaging.HELP_MESSAGE
import io.burnscommalucas.readybotlin.service.OutboundMessaging.NO_CHECK_MESSAGE
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import kotlin.jvm.optionals.getOrElse

@Component
class Handlers(
    private val checkCreationService: CheckCreationService,
    private val checkRepository: CheckRepository,
    private val lookupService: LookupService,
    private val stringResolverService: StringResolverService
) {
    fun help(event: ChatInputInteractionEvent): Mono<Void> = event.reply(HELP_MESSAGE).withEphemeral(true)

    fun docs(event: ChatInputInteractionEvent): Mono<Void> = event.reply(DOCS_MESSAGE).withEphemeral(true)

    suspend fun who(event: ChatInputInteractionEvent): Mono<Void> =
        checkRepository.getCheck(event.interaction.channelId)
            ?.let { check ->
                event.reply("Still waiting for ${stringResolverService.remainingUsersString(check)} to ready.")
                    .withEphemeral(true)
            } ?: event.reply(NO_CHECK_MESSAGE).withEphemeral(true)

    suspend fun handleNewCheck(event: ChatInputInteractionEvent): Mono<Void> {
        // TODO see if check exists first, show dialog asking to confirm overwrite
        event.deferReply().subscribe()
        return checkCreationService.createAndSaveCheck(event).then()
    }

    suspend fun handleReady(event: ChatInputInteractionEvent): Mono<Void> {
        val check = checkRepository.getCheck(event.interaction.channelId)
            ?: return event.reply("No ready check active in this channel.").withEphemeral(true)

        val member = event.interaction.member.getOrElse {
            throw IllegalStateException("Could not read author for message!")
        }

        val result = check.markUserReady(member).also { checkRepository.updateReadyUsers(check) }
        if (result.isFailure()) return event.reply(result.message).withEphemeral(true)

        val count = check.remainingCount()
        var message = "${member.mention} is ready! $count user${plural(count)} left."

        // TODO maybe separate this line to give feedback when half works?
        if (check.isCheckSatisfied() && checkRepository.deleteCheck(check) != null) {
            val authorMention = lookupService.lookupUser(check.authorId).mention
            message += "\n\nCheck complete, let's go, $authorMention!"
        }
        return event.reply(message)
    }

    suspend fun unreadyHandler(event: ChatInputInteractionEvent): Mono<Void> {
        val check = checkRepository.getCheck(event.interaction.channelId)
            ?: return event.reply("No ready check active in this channel.").withEphemeral(true)

        val member = event.interaction.member.getOrElse {
            throw IllegalStateException("Could not read author for message!")
        }

        val result = check.markUserNotReady(member).also { checkRepository.updateReadyUsers(check) }
        if (result.isFailure()) return event.reply(result.message).withEphemeral(true)

        val count = check.remainingCount()
        return event.reply("${member.mention} is not ready! $count user${plural(count)} left.")
    }
}
