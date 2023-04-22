package io.burnscommalucas.readybotlin.configuration

import org.springframework.cache.concurrent.ConcurrentMapCache
import org.springframework.cache.support.SimpleCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class CacheConfig {
    private val cacheManager = SimpleCacheManager()

    companion object {
        const val shieldControllerCacheName = "ShieldControllerCache"
    }

    @PostConstruct
    fun setup() {
        cacheManager.setCaches(listOf(ConcurrentMapCache(shieldControllerCacheName)))
    }

    @Bean()
    fun cacheManager(): SimpleCacheManager = cacheManager
}