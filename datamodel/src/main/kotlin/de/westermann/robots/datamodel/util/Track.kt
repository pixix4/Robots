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

    companion object {
        val DEFAULT = Track(0.0, 0.0)
    }
}