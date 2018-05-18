package de.westermann.robots.datamodel.util

/**
 * @author lars
 */
data class Coordinate(
        val x: Int,
        val y: Int
) {

    fun toMqtt() = "$x,$y"

    companion object {
        fun parse(data: String) = data.split(",").let {
            Coordinate(it[0].toInt(), it[1].toInt())
        }
    }
}