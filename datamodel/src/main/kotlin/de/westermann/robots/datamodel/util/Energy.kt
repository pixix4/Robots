package de.westermann.robots.datamodel.util

/**
 * @author lars
 */
data class Energy(
        val value: Double,
        val state: State
) {
    companion object {
        val UNKNOWN = Energy(0.0, State.UNKNOWN)
    }


    enum class State {
        CHARGING, DISCHARGING, UNKNOWN, NO_BATTERY
    }
}
