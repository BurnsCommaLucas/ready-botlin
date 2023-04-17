package io.burnscommalucas.readybotlin.database

import discord4j.common.util.Snowflake
import io.burnscommalucas.readybotlin.model.check.ReadyCheck

interface CheckRepository {
    /**
     * Delete the given check
     */
    suspend fun deleteCheck(check: ReadyCheck): ReadyCheck?

    /**
     * Retrieve the given check
     */
    suspend fun getCheck(channelId: Long): ReadyCheck?

    /**
     * Retrieve the given check
     */
    suspend fun getCheck(channelId: Snowflake): ReadyCheck?

    /**
     * Save the state of the given check
     */
    suspend fun saveCheck(check: ReadyCheck): ReadyCheck

    /**
     * Update just the `readyUsers` field of the check
     */
    suspend fun updateReadyUsers(check: ReadyCheck): ReadyCheck
}