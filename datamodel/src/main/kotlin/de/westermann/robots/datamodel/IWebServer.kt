package de.westermann.robots.datamodel

import de.westermann.robots.datamodel.util.Color

/**
 * @author lars
 */
interface IWebServer {
    fun bind(controllerId: Int, robotId: Int)
    fun unbind(controllerId: Int, robotId: Int)

    fun setRobotName(robotId: Int, name: String)
    fun setColor(robotId: Int, color: Color)
    fun setWhitePoint(robotId: Int, color: Color)
    fun setBlackPoint(robotId: Int, color: Color)

    fun setControllerName(controllerId: Int, name: String)

    fun setForeground(robotId: Int)
    fun setBackground(robotId: Int)
    fun setPid(robotId: Int, state: Boolean)

    fun login(password: String)
    fun logout()
}