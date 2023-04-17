package io.burnscommalucas.readybotlin.service.discord

import discord4j.common.util.Snowflake
import discord4j.core.GatewayDiscordClient
import discord4j.core.`object`.entity.User
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.stereotype.Component

@Component
class LookupService(private val client: GatewayDiscordClient) {
    suspend fun lookupUser(userId: Long): User =
        client.getUserById(Snowflake.of(userId)).awaitSingleOrNull()
            ?: throw IllegalStateException("Could not find user with id '$userId'")
}