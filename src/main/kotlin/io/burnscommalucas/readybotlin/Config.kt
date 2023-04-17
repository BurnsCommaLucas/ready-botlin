package io.burnscommalucas.readybotlin

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.mongodb.reactivestreams.client.MongoClient
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
import org.litote.kmongo.id.jackson.IdJacksonModule
import org.litote.kmongo.reactivestreams.KMongo
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration
import kotlin.properties.Delegates

@Configuration
@ConfigurationProperties(prefix = "bot")
class BotConfig {
    var roleResolveEnabled by Delegates.notNull<Boolean>()
}

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
                ClientPresence.online(
                    ClientActivity.of(
                        Activity.Type.CUSTOM,
                        "Now with role mentions!",
                        null
                    )
                )
            }
            .login()
            .awaitSingle()
    }

    suspend fun getApplicationId() = getDiscordClient().restClient.applicationId.awaitFirstOrNull()
        ?: throw IllegalStateException("Failed to retrieve ApplicationID from rest client")
}

@Configuration
class JacksonConfig {
//    @Bean
//    fun jsonCustomizer(): Jackson2ObjectMapperBuilderCustomizer {
//        return Jackson2ObjectMapperBuilderCustomizer { builder: Jackson2ObjectMapperBuilder ->
//            builder.regi
//        }
//    }

    @Primary
    @Bean
    fun objectMapper(): ObjectMapper = ObjectMapper()
        .registerModule(
            KotlinModule.Builder()
                .withReflectionCacheSize(512)
                .configure(KotlinFeature.NullToEmptyCollection, false)
                .configure(KotlinFeature.NullToEmptyMap, false)
                .configure(KotlinFeature.NullIsSameAsDefault, false)
                .configure(KotlinFeature.SingletonSupport, false)
                .configure(KotlinFeature.StrictNullChecks, false)
                .build()
        )
        .registerModule(JavaTimeModule())
        .registerModule(IdJacksonModule())
        .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
}

@Configuration
@ConfigurationProperties(prefix = "mongo")
class MongoConfig : AbstractReactiveMongoConfiguration() {
    lateinit var connString: String

    lateinit var dbName: String

    @Bean
    override fun reactiveMongoClient(): MongoClient = KMongo.createClient(connString)

    override fun getDatabaseName(): String {
        return dbName
    }
}