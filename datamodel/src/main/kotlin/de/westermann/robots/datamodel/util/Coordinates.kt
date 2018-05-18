package de.westermann.robots.datamodel.util

/**
 * @author lars
 */
data class Coordinates(
        val list: List<Coordinate>
) {

    fun toMqtt() = list.map { it.toMqtt() }.joinToString(";")

    companion object {
        fun parse(data: String) =
                Coordinates(data.split(";").map {
                    Coordinate.parse(it)
                })
    }
}