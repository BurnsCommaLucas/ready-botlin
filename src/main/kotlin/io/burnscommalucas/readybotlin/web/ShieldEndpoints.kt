package io.burnscommalucas.readybotlin.web

import discord4j.core.GatewayDiscordClient
import io.burnscommalucas.readybotlin.service.listener.StatusListeners
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod.GET
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import javax.annotation.PostConstruct

@Component
@RestController
class ShieldEndpoints(
    private val discordClient: GatewayDiscordClient,
    private val statusListeners: StatusListeners
) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @RequestMapping(value = ["/guild-count"], method = [GET])
    suspend fun getServerCount(): ResponseEntity<Any> =
        try {
            discordClient.restClient.guilds.count().awaitSingleOrNull()
                ?.let { ResponseEntity.ok(it) }
                ?: ResponseEntity.status(HttpStatus.BAD_GATEWAY).build()
        } catch (e: Exception) {
            log.error("Error while polling guild count!", e)
            ResponseEntity.internalServerError().build()
        }

    @RequestMapping(method = [GET], path = ["/health"])
    suspend fun getHealth(): ResponseEntity<Any> =
        try {
            ResponseEntity.ok(statusListeners.clientIsConnected)
        } catch (e: Exception) {
            log.error("Error while checking health!", e)
            ResponseEntity.internalServerError().build()
        }
}