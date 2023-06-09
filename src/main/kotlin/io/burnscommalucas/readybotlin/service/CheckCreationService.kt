package io.burnscommalucas.readybotlin.service

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.core.spec.InteractionReplyEditMono
import io.burnscommalucas.readybotlin.database.CheckRepository
import io.burnscommalucas.readybotlin.model.check.NumericCheck
import io.burnscommalucas.readybotlin.model.check.TargetedCheck
import io.burnscommalucas.readybotlin.model.command.CheckCommandOption
import io.burnscommalucas.readybotlin.model.command.CheckCommandOption.COUNT
import io.burnscommalucas.readybotlin.model.command.CheckCommandOption.MENTIONS
import io.burnscommalucas.readybotlin.model.command.Command.CHECK
import io.burnscommalucas.readybotlin.model.command.Command.READY
import io.burnscommalucas.readybotlin.plural
import io.burnscommalucas.readybotlin.service.discord.LookupService
import io.burnscommalucas.readybotlin.service.discord.StringResolverService
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class CheckCreationService(
    private val checkRepository: CheckRepository,
    private val stringResolverService: StringResolverService,
    private val lookupService: LookupService
) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    suspend fun createAndSaveCheck(event: ChatInputInteractionEvent): Mono<out Any> {
        val invalidParamsMessage: () -> InteractionReplyEditMono = {
            event.editReply("You'll need to run either `/$CHECK $COUNT` or `/$CHECK $MENTIONS` to create a check.")
        }

        if (event.options.size != 1) return invalidParamsMessage()

        val check = when (CheckCommandOption.valueOfOrNull(event.options.first().name)) {
            COUNT -> numericCheckFromEvent(event)
            MENTIONS -> targetedCheckFromEvent(event)
            // Shouldn't really be possible to get here with slash commands but better safe than sorry
            else -> return invalidParamsMessage()
        }

        return if (check != null) {
            val count = check.remainingCount()
            val mentions = stringResolverService.remainingUsersString(check)
            val plural = plural(count)
            val authorMention = event.interaction.user.mention

            val message = "$mentions ready up! Type `/$READY`. $authorMention is waiting for $count user$plural."

            checkRepository.saveCheck(check).let { event.editReply(message) }
        } else Mono.empty()
    }

    private suspend fun targetedCheckFromEvent(event: ChatInputInteractionEvent): TargetedCheck? {
        // Resolve all directly mentioned users who are not bots
        val userMentions = try {
            event.interaction
                .commandInteraction.get()
                .resolved.get()
                .members
                .values
                .map { it.asFullMember().awaitSingle() }
                .filter { !it.isBot }
        } catch (_: NoSuchElementException) {
            listOf()
        }

        // Resolve all users mentioned via a custom role
        val roleMentions = try {
            val guild = event.interaction.guild.awaitSingle()

            event.interaction
                .commandInteraction.get()
                .resolved.get()
                .roles
                .values
                .flatMap { role ->
                    if (!role.isEveryone) lookupService.findMembersWithRole(guild, role)
                    else listOf()
                }
                .filter { !it.isBot }
        } catch (_: NoSuchElementException) {
            listOf()
        }

        val allMentions = (userMentions + roleMentions).toSet()

        if (allMentions.isEmpty()) {
            event.editReply(
                """
                You'll need to select some users to create a `$MENTIONS` check. Keep in mind I can't wait for bots or `@everyone/@here`.
                
                If you'd like to wait for a number of users rather than specific users, use `/$CHECK $COUNT` instead.
                """.trimIndent()
            ).awaitSingleOrNull()
            return null
        }

        return TargetedCheck(
            channelId = event.interaction.channelId,
            authorId = event.interaction.user.id,
            targetUsers = allMentions.map { it.id.asLong() }
        )
    }

    private suspend fun numericCheckFromEvent(event: ChatInputInteractionEvent): NumericCheck? {
        try {
            val checkCount = event.options.first().value.get().asLong()
            if (checkCount < 1) {
                event.editReply(
                    """
                    Sorry, I can only wait for one or more users with a `$COUNT` check. Try creating your check with a count of at least 1.
                    
                    If you'd like to wait for specific users rather than a number, use `/$CHECK $MENTIONS`
                    """.trimIndent()
                ).subscribe()
                return null
            }

            return NumericCheck(
                channelId = event.interaction.channelId,
                authorId = event.interaction.user.id,
                targetCount = checkCount
            )
        } catch (e: Exception) {
            log.error("Could not create Numeric check", e)
            return null
        }
    }
}