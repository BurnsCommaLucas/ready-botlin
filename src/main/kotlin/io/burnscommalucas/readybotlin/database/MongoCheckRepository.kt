package io.burnscommalucas.readybotlin.database

import discord4j.common.util.Snowflake
import io.burnscommalucas.readybotlin.MongoConfig
import io.burnscommalucas.readybotlin.model.check.ReadyCheck
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.litote.kmongo.eq
import org.litote.kmongo.reactivestreams.getCollection
import org.litote.kmongo.reactor.findOne
import org.litote.kmongo.reactor.save
import org.litote.kmongo.set
import org.litote.kmongo.setTo
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component

@Component
@Primary
class MongoCheckRepository(mongoConfig: MongoConfig) : CheckRepository {
    private val collection =
        mongoConfig.reactiveMongoClient()
            .getDatabase(mongoConfig.dbName)
            .getCollection<ReadyCheck>()

    override suspend fun deleteCheck(check: ReadyCheck): ReadyCheck? =
        collection.findOneAndDelete(ReadyCheck::channelId eq check.channelId)
            .awaitFirstOrNull()

    override suspend fun getCheck(channelId: Long): ReadyCheck? =
        collection.findOne(ReadyCheck::channelId eq channelId)
            .awaitFirstOrNull()

    override suspend fun getCheck(channelId: Snowflake): ReadyCheck? = getCheck(channelId.asLong())

    override suspend fun saveCheck(check: ReadyCheck): ReadyCheck =
        collection.save(check).awaitSingleOrNull().let { check }

    override suspend fun updateReadyUsers(check: ReadyCheck): ReadyCheck =
        collection.findOneAndUpdate(
            ReadyCheck::channelId eq check.channelId,
            set(ReadyCheck::readyMembers setTo check.readyMembers)
        ).awaitFirstOrNull()?.let {
            check
        } ?: throw IllegalStateException("Failed to update ready users")
}