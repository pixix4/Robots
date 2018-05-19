package de.westermann.robots.datamodel

import de.westermann.robots.datamodel.util.Button
import de.westermann.robots.datamodel.util.Track

/**
 * @author lars
 */
interface IController {
    fun onTrack(track: Track)
    fun onAbsoluteSpeed(speed: Double)
    fun onRelativeSpeed(deltaSpeed: Double)
    fun onButton(button: Button)
}