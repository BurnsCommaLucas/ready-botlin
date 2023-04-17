package io.burnscommalucas.readybotlin.service.discord

import io.burnscommalucas.readybotlin.model.check.NumericCheck
import io.burnscommalucas.readybotlin.model.check.ReadyCheck
import io.burnscommalucas.readybotlin.model.check.TargetedCheck
import io.burnscommalucas.readybotlin.plural
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Component

@Component
class StringResolverService(private val lookupService: LookupService) {
    fun remainingUsersString(check: ReadyCheck): String = when (check) {
        is NumericCheck -> {
            val count = check.remainingCount()
            "$count user${plural(count)}"
        }

        is TargetedCheck -> check.remainingUsers()
            .joinToString(", ") {
                runBlocking {
                    lookupService.lookupUser(it).mention
                }
            }

        else -> throw IllegalStateException("Unknown check type!")
    }
}