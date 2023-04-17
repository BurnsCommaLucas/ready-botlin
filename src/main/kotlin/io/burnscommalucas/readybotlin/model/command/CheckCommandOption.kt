package io.burnscommalucas.readybotlin.model.command

enum class CheckCommandOption {
    COUNT,
    MENTIONS;

    companion object {
        fun valueOfOrNull(value: String): CheckCommandOption? {
            return CheckCommandOption.values().firstOrNull { it.name.lowercase() == value.lowercase() }
        }
    }

    override fun toString(): String = name.lowercase()
}