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
    }

    enum class State {
        RUNNING, DISABLED, UNAVAILABLE
    }
}
