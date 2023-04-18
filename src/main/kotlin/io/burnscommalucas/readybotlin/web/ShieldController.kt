package io.burnscommalucas.readybotlin.web

import discord4j.core.GatewayDiscordClient
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod.GET
import org.springframework.web.bind.annotation.RestController
import java.time.Duration

@Component
@RestController
class ShieldController(
    private val discordClient: GatewayDiscordClient
) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @RequestMapping(value = ["/guild-count"], method = [GET])
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