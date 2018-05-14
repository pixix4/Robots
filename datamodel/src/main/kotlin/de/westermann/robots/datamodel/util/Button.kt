package de.westermann.robots.datamodel.util

/**
 * @author lars
 */
data class Button(
        val type: Type,
        val state: State
) {

    enum class Type {
        A, B, UNKNOWN
    }

    enum class State {
        DOWN, UP, PRESS
    }

    fun toJson() = json {
        value("type") { type.name }
        value("state") { state.name }
    }

    companion object {
        fun fromJson(json: Json) = Button(
                json["type"]?.toString()?.let { s ->
                    Type.values().find { it.name.equals(s, ignoreCase = true) }
                } ?: Type.UNKNOWN,
                json["state"]?.toString()?.let { s ->
                    State.values().find { it.name.equals(s, ignoreCase = true) }
                } ?: State.PRESS
        )
    }
}