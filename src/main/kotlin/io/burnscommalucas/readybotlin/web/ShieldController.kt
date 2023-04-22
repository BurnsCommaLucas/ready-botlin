package io.burnscommalucas.readybotlin.web

import discord4j.core.GatewayDiscordClient
import io.burnscommalucas.readybotlin.configuration.CacheConfig
import io.burnscommalucas.readybotlin.configuration.CacheConfig.Companion.shieldControllerCacheName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.DependsOn
import org.springframework.http.ResponseEntity
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.TimeUnit.MINUTES
import javax.annotation.PostConstruct

@RestController
@DependsOn("cacheManager")
@EnableScheduling
class ShieldController(
    private val discordClient: GatewayDiscordClient,
    cacheConfig: CacheConfig
) {
    private val log = LoggerFactory.getLogger(this.javaClass)
    private val scope = CoroutineScope(Dispatchers.Default)
    private val cache = cacheConfig.cacheManager().getCache(shieldControllerCacheName)
        ?: throw IllegalStateException("Couldn't find cache for shield controller of name '$shieldControllerCacheName'")

    companion object {
        private const val guildCountCacheKey = "guild-count"
    }

    @PostConstruct
    fun setup() = scope.launch {
        // init cache
        updateSavedGuildCount()
    }

    @GetMapping("/guild-count")
    suspend fun getServerCount(): ResponseEntity<Long> =
        cache[guildCountCacheKey]?.let {
            ResponseEntity.ok(it.get() as Long)
        } ?: run {
            ResponseEntity.internalServerError().build()
        }

    @Scheduled(fixedDelay = 5, timeUnit = MINUTES)
    fun updateSavedGuildCount() =
        try {
            log.debug("Updating guild count cache...")
            runBlocking { discordClient.restClient.guilds.count().awaitSingleOrNull() }
        } catch (e: Exception) {
            log.error("Error while polling guild count!", e)
            null
        } finally {
            log.debug("Finished updating guild count cache.")
        }?.let {
            cache.put(guildCountCacheKey, it)
        } ?: run {
            log.warn("Received null guild count on fetch")
        }
}