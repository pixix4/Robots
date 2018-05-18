package de.westermann.robots.datamodel

/**
 * @author lars
 */
interface IWebClient {
    fun addRobot(robot: Robot)
    fun updateRobot(robot: Robot)
    fun removeRobot(robot: Robot)

    fun addController(controller: Controller)
    fun updateController(controller: Controller)
    fun removeController(controller: Controller)

    fun bind(controllerId: Int, robotId: Int)
    fun unbind(controllerId: Int, robotId: Int)

    fun login()
    fun logout()
}