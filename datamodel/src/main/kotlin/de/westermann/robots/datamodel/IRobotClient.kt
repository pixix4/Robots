package de.westermann.robots.datamodel

import de.westermann.robots.datamodel.util.Color
import de.westermann.robots.datamodel.util.Track

/**
 * @author lars
 */
interface IRobotClient {
    fun setForegroundColor()
    fun setBackgroundColor()

    fun resetMap()

    fun pid(enable: Boolean)

    fun speed(speed: Double)
    fun track(track: Track)
    fun trim(trim: Double)
    fun kick()

    fun setName(name: String)
    fun setColor(color: Color)
}