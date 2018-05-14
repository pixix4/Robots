package de.westermann.robots.datamodel.util

/**
 * @author lars
 */
data class LineFollower(
        val state: State,
        val left: Color,
        val right: Color
) {
    companion object {
        val UNKNOWN = LineFollower(State.UNAVAILABLE, Color.TRANSPARENT, Color.TRANSPARENT)

        fun fromJson(json: Json) = LineFollower(
                json["state"]?.toString()?.let { s ->
                    State.values().find { it.name.equals(s, ignoreCase = true) }
                } ?: State.UNAVAILABLE,
                try {Color.parse(json["left"]?.toString() ?: "")} catch (_: IllegalArgumentException) {Color.TRANSPARENT},
                try {Color.parse(json["right"]?.toString() ?: "")} catch (_: IllegalArgumentException) {Color.TRANSPARENT}
        )
    }

    enum class State {
        RUNNING, DISABLED, UNAVAILABLE
    }

    fun toJson() = json {
        value("state") {state.name}
        value("left") {left.toString()}
        value("right") {right.toString()}
    }
}
