package io.burnscommalucas.readybotlin.model.command

import discord4j.core.`object`.command.ApplicationCommandOption
import discord4j.discordjson.json.ApplicationCommandOptionData

data class CommandOptionParam(
    val name: String,
    val type: ApplicationCommandOption.Type,
    val description: String
) {
    fun toApplicationCommandOptionData(): ApplicationCommandOptionData =
        ApplicationCommandOptionData.builder()
            .name(name)
            .type(type.value)
            .description(description)
            .build()
}