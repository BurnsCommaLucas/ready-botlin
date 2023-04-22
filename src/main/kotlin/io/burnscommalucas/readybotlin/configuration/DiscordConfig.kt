package io.burnscommalucas.readybotlin.configuration

import discord4j.core.DiscordClient
import discord4j.core.GatewayDiscordClient
import discord4j.core.`object`.presence.Activity
import discord4j.core.`object`.presence.ClientActivity
import discord4j.core.`object`.presence.ClientPresence
import discord4j.core.`object`.presence.Status
import discord4j.gateway.intent.Intent
import discord4j.gateway.intent.IntentSet
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "discord")
class DiscordConfig {
    lateinit var token: String

    @Bean
    fun getDiscordClient(): GatewayDiscordClient = runBlocking {
        DiscordClient.create(token)
            .gateway()
            .setEnabledIntents(IntentSet.of(Intent.GUILD_MEMBERS))
            .setInitialPresence {
                ClientPresence.of(
                    Status.ONLINE,
                    // Bots can't use the CUSTOM type (😡) so PLAYING will have to do
                    ClientActivity.of(
                        Activity.Type.PLAYING,
                        "Now with role mentions!",
                        null
                    )
                )
            }
            .login()
            .awaitSingle()
    }
}