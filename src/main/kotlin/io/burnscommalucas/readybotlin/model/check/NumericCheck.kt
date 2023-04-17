package io.burnscommalucas.readybotlin.model.check

import discord4j.common.util.Snowflake
import discord4j.core.`object`.entity.Member
import io.burnscommalucas.readybotlin.model.check.ReadyCheck.ReadyResponse.FAIL_ALREADY_READY
import io.burnscommalucas.readybotlin.model.check.ReadyCheck.ReadyResponse.FAIL_NOT_YET_READY
import io.burnscommalucas.readybotlin.model.check.ReadyCheck.ReadyResponse.SUCCESS_NOT_READY
import io.burnscommalucas.readybotlin.model.check.ReadyCheck.ReadyResponse.SUCCESS_READY

data class NumericCheck(
    override val channelId: Long,
    override val authorId: Long,
    val targetCount: Long,
    override val readyMembers: MutableSet<Long> = mutableSetOf()
) : ReadyCheck(channelId, authorId, readyMembers) {
    constructor(channelId: Snowflake, authorId: Snowflake, targetCount: Long) : this(
        channelId.asLong(),
        authorId.asLong(),
        targetCount
    )

    override fun isCheckSatisfied(): Boolean = readyMembers.size.toLong() == targetCount

    override fun markUserNotReady(member: Member): ReadyResponse {
        if (!readyMembers.contains(member.id.asLong())) return FAIL_NOT_YET_READY

        readyMembers.remove(member.id.asLong())

        return SUCCESS_NOT_READY
    }

    override fun markUserReady(member: Member): ReadyResponse {
        if (readyMembers.contains(member.id.asLong())) return FAIL_ALREADY_READY

        readyMembers.add(member.id.asLong())

        return SUCCESS_READY
    }

    override fun remainingCount(): Long = targetCount - readyMembers.size.toLong()
}