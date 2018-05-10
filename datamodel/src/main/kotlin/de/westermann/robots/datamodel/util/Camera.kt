package de.westermann.robots.datamodel.util

/**
 * @author lars
 */

data class Camera(
        override val available: Boolean,
        val stream: String = ""
): RobotModule {
    companion object {
        val NONE = Camera(false)
    }
}
