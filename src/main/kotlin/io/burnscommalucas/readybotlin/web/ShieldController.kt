package io.burnscommalucas.readybotlin.web

import discord4j.core.GatewayDiscordClient
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Duration

@RestController
class ShieldController(
    private val discordClient: GatewayDiscordClient
) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @GetMapping("/guild-count")
    suspend fun getServerCount(): ResponseEntity<Any> =
        try {
            discordClient.restClient.guilds.count().cache(Duration.ofMinutes(5))
                .awaitSingleOrNull()
                ?.let { ResponseEntity.ok(it) }
                ?: run {
                    log.warn("Received null guild count on fetch")
                    ResponseEntity.status(HttpStatus.BAD_GATEWAY).build()
                }
        } catch (e: Exception) {
            log.error("Error while polling guild count!", e)
            ResponseEntity.internalServerError().build()
        }
}