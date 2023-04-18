package io.burnscommalucas.readybotlin.configuration

import discord4j.core.DiscordClient
import discord4j.core.GatewayDiscordClient
import discord4j.core.`object`.presence.Activity
import discord4j.core.`object`.presence.ClientActivity
import discord4j.core.`object`.presence.ClientPresence
import discord4j.core.`object`.presence.Status
import discord4j.gateway.intent.Intent
import discord4j.gateway.intent.IntentSet
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "discord")
class DiscordConfig {
    private val log = LoggerFactory.getLogger(this.javaClass)

    var roleResolveEnabled: Boolean = false
    lateinit var token: String

    @Bean
    fun getDiscordClient(): GatewayDiscordClient = runBlocking {
        DiscordClient.create(token)
            .gateway()
            .let {
                val buildPresenceWithText: (String) -> ClientPresence = { text ->
                    ClientPresence.of(
                        Status.ONLINE,
                        // Bots can't use the CUSTOM type (😡) so PLAYING will have to do
                        ClientActivity.of(Activity.Type.PLAYING, text, null)
                    )
                }

                if (roleResolveEnabled) {
                    log.info("Role Resolution enabled, requesting ${Intent.GUILD_MEMBERS.name} intent")
                    it.setEnabledIntents(IntentSet.of(Intent.GUILD_MEMBERS))
                        .setInitialPresence { buildPresenceWithText("Now with role mentions!") }
                } else {
                    log.info("Role Resolution disabled, skipping ${Intent.GUILD_MEMBERS.name} intent")
                    it.setEnabledIntents(IntentSet.none())
                        .setInitialPresence { buildPresenceWithText("/check") }
                }
            }
            .login()
            .awaitSingle()
    }

    suspend fun getApplicationId() = getDiscordClient().restClient.applicationId.awaitFirstOrNull()
        ?: throw IllegalStateException("Failed to retrieve ApplicationID from rest client")
}