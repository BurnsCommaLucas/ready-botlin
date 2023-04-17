package io.burnscommalucas.readybotlin.service

import discord4j.common.util.Snowflake
import discord4j.core.GatewayDiscordClient
import discord4j.core.`object`.entity.User
import discord4j.core.`object`.entity.channel.Channel
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.stereotype.Component

@Component
class LookupService(private val client: GatewayDiscordClient) {
    //    suspend fun lookupChannel(guildId: String, channelId: String): Channel =
//        client.guilds.toFlux().filter { it.id.toString() == guildId }.awaitFirstOrNull()
//            ?.let { guild ->
//                guild.channels.toFlux().filter { it.id.toString() == channelId }.awaitFirstOrNull()
//                    ?: throw IllegalStateException("Could not find channel with id '$channelId'")
//            }
//            ?: throw IllegalStateException("Could not find guild with id '$guildId'")
    suspend fun lookupChannel(channelId: Long): Channel =
        client.getChannelById(Snowflake.of(channelId)).awaitSingleOrNull()
            ?: throw IllegalStateException("Could not find channel with id '$channelId'")

    suspend fun lookupUser(userId: Long): User =
        client.getUserById(Snowflake.of(userId)).awaitSingleOrNull()
            ?: throw IllegalStateException("Could not find user with id '$userId'")

    suspend fun lookupUsers(userIds: Collection<Long>): List<User> =
        userIds.map { lookupUser(it) }
}