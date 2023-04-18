package io.burnscommalucas.readybotlin.configuration

import discord4j.core.DiscordClient
import discord4j.core.GatewayDiscordClient
import discord4j.core.`object`.presence.Activity
import discord4j.core.`object`.presence.ClientActivity
import discord4j.core.`object`.presence.ClientPresence
import discord4j.gateway.intent.Intent
import discord4j.gateway.intent.IntentSet
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import kotlin.properties.Delegates

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
                if (roleResolveEnabled) {
                    log.info("Role Resolution enabled, requesting ${Intent.GUILD_MEMBERS.name} intent")
                    it.setEnabledIntents(IntentSet.of(Intent.GUILD_MEMBERS))
                        .setInitialPresence {
                            ClientPresence.online(
                                ClientActivity.of(
                                    Activity.Type.CUSTOM,
                                    "Now with role mentions!",
                                    null
                                )
                            )
                        }
                } else {
                    log.info("Role Resolution disabled, skipping ${Intent.GUILD_MEMBERS.name} intent")
                    it.setDisabledIntents(IntentSet.all())
                }
            }
            .login()
            .awaitSingle()
    }

    suspend fun getApplicationId() = getDiscordClient().restClient.applicationId.awaitFirstOrNull()
        ?: throw IllegalStateException("Failed to retrieve ApplicationID from rest client")
}