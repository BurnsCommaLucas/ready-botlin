package io.burnscommalucas.readybotlin.service

import io.burnscommalucas.readybotlin.service.listener.StatusListeners
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.stereotype.Component

@Component
class ClientHealthIndicator(private val statusListeners: StatusListeners) : HealthIndicator {
    override fun health(): Health =
        if (statusListeners.clientIsConnected) {
            Health.up()
        } else {
            Health.down()
        }.build()
}