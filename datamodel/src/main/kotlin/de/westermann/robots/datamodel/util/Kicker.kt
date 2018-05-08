package de.westermann.robots.datamodel.util

/**
 * @author lars
 */

data class Kicker(
        val available: Boolean
) {

    companion object {
        val NONE = Kicker(false)
    }
}
