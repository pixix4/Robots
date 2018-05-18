package de.westermann.robots.datamodel.util

import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.sqrt

/**
 * @author lars
 */
data class Track(
        val x: Double,
        val y: Double
) {

    val radius: Double = sqrt(x * x + y * y)
    val angle: Double = if (y >= 0) {
        acos(x / radius)
    } else {
        2 * PI - acos(x / radius)
    }

    fun toJson() = json {
        value("x") { x }
        value("y") { y }
    }

    fun toMqtt() = "$x,$y"

    companion object {
        val DEFAULT = Track(0.0, 0.0)

        fun parse(data: String) = data.split(",").let {
            Track(it[0].toDouble(), it[1].toDouble())
        }

        fun fromJson(json: Json) = Track(
                json["x"]?.toString()?.toDoubleOrNull() ?: 0.0,
                json["y"]?.toString()?.toDoubleOrNull() ?: 0.0
        )
    }
}