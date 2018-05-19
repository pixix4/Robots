package de.westermann.robots.datamodel

import de.westermann.robots.datamodel.util.Color
import de.westermann.robots.datamodel.util.Track

/**
 * @author lars
 */
interface IRobotClient {
    fun foregroundColor(color: Color?)
    fun backgroundColor(color: Color?)

    fun resetMap()

    fun pid(enable: Boolean)

    fun speed(speed: Double)
    fun track(track: Track)
    fun trim(trim: Double)
}