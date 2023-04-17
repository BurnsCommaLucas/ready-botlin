package io.burnscommalucas.readybotlin.model.check

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import discord4j.core.`object`.entity.Member
import org.bson.codecs.pojo.annotations.BsonId

@JsonSubTypes(
    JsonSubTypes.Type(value = NumericCheck::class),
    JsonSubTypes.Type(value = TargetedCheck::class)
)
@JsonTypeInfo(
    use = JsonTypeInfo.Id.DEDUCTION
//    include = JsonTypeInfo.As.PROPERTY,
//    property = "checkType"
)
abstract class ReadyCheck(
    @BsonId
    open val channelId: Long,
    open val authorId: Long,
    open val readyMembers: MutableSet<Long>
) {
    @JsonIgnore
    abstract fun isCheckSatisfied(): Boolean

    abstract fun markUserNotReady(member: Member): ReadyResponse

    abstract fun markUserReady(member: Member): ReadyResponse

    abstract fun remainingCount(): Long

    enum class ReadyResponse(val message: String = "") {
        FAIL_NOT_NEEDED("You don't need to ready in this check!"),
        FAIL_ALREADY_READY("You've already readied!"),
        FAIL_NOT_YET_READY("You haven't readied yet, no need to unready!"),
        SUCCESS_READY,
        SUCCESS_NOT_READY;

        fun isFailure(): Boolean = name.startsWith("FAIL")
    }
}