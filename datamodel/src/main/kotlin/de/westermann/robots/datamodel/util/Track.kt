package de.westermann.robots.datamodel.util

import kotlin.math.sqrt

/**
 * @author lars
 */
data class Track(
        val x: Double,
        val y: Double
) {

    val radius: Double = sqrt(x * x + y * y)

    fun toJson() = json {
        value("x") { x }
        value("y") { y }
    }


    companion object {
        val DEFAULT = Track(0.0, 0.0)

        fun fromJson(json: Json) = Track(
                json["x"]?.toString()?.toDoubleOrNull() ?: 0.0,
                json["y"]?.toString()?.toDoubleOrNull() ?: 0.0
        )
    }
}