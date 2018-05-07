package de.westermann.robots.datamodel.util

/**
 * @author lars
 */
data class Color(
        val red: Int,
        val gree: Int,
        val blue: Int
) {
    companion object {
        val NONE = Color(-1, -1, -1)
    }
}
