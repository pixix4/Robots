package de.westermann.robots.datamodel

import de.westermann.robots.datamodel.util.Color

/**
 * @author lars
 */
interface IWebServer {
    fun bind(controllerId: Int, robotId: Int)
    fun unbind(controllerId: Int, robotId: Int)

    fun setName(robotId: Int, name: String)
    fun setColor(robotId: Int, color: Color)

    fun setForeground(robotId: Int)
    fun setBackground(robotId: Int)

    fun login(password: String)
    fun logout()
}