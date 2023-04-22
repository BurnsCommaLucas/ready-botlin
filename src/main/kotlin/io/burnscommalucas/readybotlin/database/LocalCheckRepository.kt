package io.burnscommalucas.readybotlin.database

import com.fasterxml.jackson.databind.ObjectMapper
import discord4j.common.util.Snowflake
import io.burnscommalucas.readybotlin.model.check.ReadyCheck
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository

@Repository
class LocalCheckRepository(private val objectMapper: ObjectMapper) : CheckRepository {
    private val log = LoggerFactory.getLogger(this.javaClass)
    val checkMap = mutableMapOf<Long, ReadyCheck>()

    override suspend fun deleteCheck(check: ReadyCheck): ReadyCheck? {
        log.info(objectMapper.writeValueAsString(check))
        return checkMap.remove(check.channelId)
    }

    override suspend fun getCheck(channelId: Long): ReadyCheck? {
        log.info(objectMapper.writeValueAsString(channelId))
        return checkMap[channelId]
    }

    override suspend fun getCheck(channelId: Snowflake): ReadyCheck? = getCheck(channelId.asLong())

    override suspend fun saveCheck(check: ReadyCheck): ReadyCheck {
        log.info(objectMapper.writeValueAsString(check))
        checkMap[check.channelId] = check
        return check
    }

    override suspend fun updateReadyUsers(check: ReadyCheck): ReadyCheck {
        log.info(objectMapper.writeValueAsString(check))
        checkMap[check.channelId] = check
        return check
    }
}