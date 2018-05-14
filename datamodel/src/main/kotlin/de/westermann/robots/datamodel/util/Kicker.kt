package de.westermann.robots.datamodel.util

/**
 * @author lars
 */

data class Kicker(
        override val available: Boolean
) : RobotModule {

    companion object {
        val NONE = Kicker(false)

        fun fromJson(json: Json) = Kicker(
                json["available"]?.toString()?.toBoolean() ?: false
        )
    }

    fun toJson() = json {
        value("available") { available }
    }
}
