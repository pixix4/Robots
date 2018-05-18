package de.westermann.robots.datamodel.util

/**
 * @author lars
 */
data class LineFollower(
        val state: State,
        val foreground: Color,
        val background: Color
) {
    companion object {
        val UNKNOWN = LineFollower(State.UNAVAILABLE, Color.TRANSPARENT, Color.TRANSPARENT)

        fun fromJson(json: Json) = LineFollower(
                json["state"]?.toString()?.let { s ->
                    State.values().find { it.name.equals(s, ignoreCase = true) }
                } ?: State.UNAVAILABLE,
                try {Color.parse(json["foreground"]?.toString() ?: "")} catch (_: IllegalArgumentException) {Color.TRANSPARENT},
                try {Color.parse(json["background"]?.toString() ?: "")} catch (_: IllegalArgumentException) {Color.TRANSPARENT}
        )
    }

    enum class State {
        RUNNING, DISABLED, UNAVAILABLE
    }

    fun toJson() = json {
        value("state") {state.name}
        value("foreground") {foreground.toString()}
        value("background") {background.toString()}
    }
}
