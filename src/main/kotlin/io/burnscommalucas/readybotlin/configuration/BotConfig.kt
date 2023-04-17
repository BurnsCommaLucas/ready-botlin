package io.burnscommalucas.readybotlin.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import kotlin.properties.Delegates

@Configuration
@ConfigurationProperties(prefix = "bot")
class BotConfig {
    var roleResolveEnabled by Delegates.notNull<Boolean>()
}