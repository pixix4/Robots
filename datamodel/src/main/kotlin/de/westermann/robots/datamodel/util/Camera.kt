package de.westermann.robots.datamodel.util

/**
 * @author lars
 */

data class Camera(
        override val available: Boolean,
        val stream: String = ""
) : RobotModule {
    companion object {
        val NONE = Camera(false)

        fun fromJson(json: Json) = Camera(
                json["available"]?.toString()?.toBoolean() ?: false,
                json["stream"]?.toString() ?: ""
        )
    }

    fun toJson() = json {
        value("available") { available }
        value("stream") { stream }
    }
}
