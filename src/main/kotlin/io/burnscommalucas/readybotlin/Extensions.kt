package io.burnscommalucas.readybotlin

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import kotlinx.coroutines.reactor.awaitSingle

fun plural(count: Number, pluralizer: String = "s"): String = if (count.toLong() > 1) pluralizer else ""

suspend fun ChatInputInteractionEvent.hasEmptyReply(): Boolean =
    this.reply.map { it.content.isNullOrEmpty() }.awaitSingle()