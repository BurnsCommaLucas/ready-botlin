package io.burnscommalucas.readybotlin.model.command

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import discord4j.core.`object`.command.ApplicationCommandOption
import discord4j.discordjson.json.ApplicationCommandRequest

enum class Command(private val description: String, private val options: List<CommandOptionParam> = listOf()) {
    CHECK(
        "Create a ready check",
        listOf(
            CommandOptionParam(
                CheckCommandOption.COUNT.toString(),
                ApplicationCommandOption.Type.INTEGER,
                "The number of users to ready"
            ),
            CommandOptionParam(
                CheckCommandOption.MENTIONS.toString(),
                ApplicationCommandOption.Type.STRING,
                "The users/roles to ready"
            )
        )
    ),
    READY("Respond \"Ready\" to a ready check"),
    UNREADY("Respond \"Not Ready\" to a ready check"),
    WHO("See who still needs to ready"),
    HELP("Get help using ready-bot"),
    DOCS("Get details about ready-bot and how to contribute to its development");

    companion object {
        fun valueOfOrNull(value: String): Command? {
            return Command.values().firstOrNull { it.name.lowercase() == value.lowercase() }
        }
    }

    fun getApplicationCommandRequest(): ApplicationCommandRequest =
        ApplicationCommandRequest.builder()
            .name(name.lowercase())
            .description(description)
            .addAllOptions(options.map { it.toApplicationCommandOptionData() })
            .build()

    fun prettyJson(): String {
        val map = listOf(
            Command::description.name to description,
            Command::options.name to options.map {
                mapOf(
                    CommandOptionParam::name.name to it.name,
                    CommandOptionParam::type.name to it.type,
                    CommandOptionParam::description.name to it.description
                )
            }
        ).filter { (_, v) ->
            (v is Collection<*> && !v.isEmpty()) || v !is Collection<*>
        }.toMap()

        return jacksonObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(map)
    }

    override fun toString(): String = name.lowercase()
}