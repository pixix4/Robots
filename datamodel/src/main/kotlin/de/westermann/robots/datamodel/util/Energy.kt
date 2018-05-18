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

        fun parse(data: String) = data.split(",").let {
            Energy(it[0].toDouble(), State.valueOf(it[1]))
        }

        fun fromJson(json: Json) = Energy(
                json["value"]?.toString()?.toDoubleOrNull() ?: 0.0,
                json["state"]?.toString()?.let { s ->
                    State.values().find { it.name.equals(s, ignoreCase = true) }
                } ?: State.UNKNOWN
        )
    }

    fun toMqtt() = "$value,$state"

    fun toJson() = json {
        value("value") { value }
        value("state") { state.name }
    }


    enum class State {
        CHARGING, DISCHARGING, UNKNOWN, NO_BATTERY
    }
}
