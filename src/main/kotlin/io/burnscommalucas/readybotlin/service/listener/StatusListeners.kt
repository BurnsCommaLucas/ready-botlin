package io.burnscommalucas.readybotlin.service.listener

import discord4j.core.GatewayDiscordClient
import discord4j.core.event.domain.lifecycle.ConnectEvent
import discord4j.core.event.domain.lifecycle.DisconnectEvent
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import javax.annotation.PostConstruct

@Component
class StatusListeners(
    private val discordClient: GatewayDiscordClient,
) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    // assume we start connected, since failure to connect means the program won't start
    private var _clientIsConnected = true
    val clientIsConnected: Boolean
        get() = _clientIsConnected

    @PostConstruct
    fun applyListeners() {
        discordClient.on(ConnectEvent::class.java) { connectListener() }.subscribe()
        discordClient.on(DisconnectEvent::class.java) { disconnectListener() }.subscribe()
    }

    fun connectListener(): Mono<Void> =
        try {
            _clientIsConnected = true
            Mono.empty()
        } catch (e: Exception) {
            log.error("Error while monitoring connect events!", e)
            Mono.empty()
        }

    fun disconnectListener(): Mono<Void> =
        try {
            _clientIsConnected = false
            Mono.empty()
        } catch (e: Exception) {
            log.error("Error while monitoring connect events!", e)
            Mono.empty()
        }
}