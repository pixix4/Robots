package de.westermann.robots.datamodel

import de.westermann.robots.datamodel.util.Button
import de.westermann.robots.datamodel.util.Track

/**
 * @author lars
 */
interface IWebServer {
    fun onTrack(track: Track)
    fun onAbsoluteSpeed(speed: Double)
    fun onRelativeSpeed(deltaSpeed: Double)
    fun onButton(button: Button)

    fun bind(controllerId: Int, robotId: Int)
    fun unbind(controllerId: Int, robotId: Int)
    fun login(password: String)
    fun logout()
}