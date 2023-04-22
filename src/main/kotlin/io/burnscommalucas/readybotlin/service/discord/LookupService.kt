package io.burnscommalucas.readybotlin.service.discord

import discord4j.common.util.Snowflake
import discord4j.core.GatewayDiscordClient
import discord4j.core.`object`.entity.Guild
import discord4j.core.`object`.entity.Member
import discord4j.core.`object`.entity.Role
import discord4j.core.`object`.entity.User
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.stereotype.Service

@Service
class LookupService(private val client: GatewayDiscordClient) {
    suspend fun findUserById(userId: Long): User =
        client.getUserById(Snowflake.of(userId)).awaitSingleOrNull()
            ?: throw IllegalStateException("Could not find user with id '$userId'")

    suspend fun findMembersWithRole(guild: Guild, targetRole: Role): Set<Member> =
        guild.members.collectList()
            .awaitSingle()
            .associateWith { member -> member.roles.collectList().awaitSingle() }
            .filter { (_, roles) -> roles.contains(targetRole) }
            .keys
}