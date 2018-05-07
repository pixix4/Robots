package de.westermann.robots.datamodel.util

/**
 * @author lars
 */

data class Camera(
        val available: Boolean,
        val stream: String = ""
) {
    companion object {
        val UNKNOWN = Camera(false)
    }
}
