package io.burnscommalucas.readybotlin.configuration

import com.mongodb.reactivestreams.client.MongoClient
import org.litote.kmongo.reactivestreams.KMongo
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration

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