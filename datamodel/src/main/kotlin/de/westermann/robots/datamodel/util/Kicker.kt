package de.westermann.robots.datamodel.util

/**
 * @author lars
 */

data class Kicker(
        override val available: Boolean
): RobotModule {

    companion object {
        val NONE = Kicker(false)
    }
}
