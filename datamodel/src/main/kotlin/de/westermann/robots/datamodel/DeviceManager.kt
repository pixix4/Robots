package de.westermann.robots.datamodel

import de.westermann.robots.datamodel.observe.Library

/**
 * @author lars
 */
object DeviceManager {
    val robots = Library<Robot>()
    val controllers = Library<Controller>()

    private var controllerToRobots: Map<Controller, Set<Robot>> = emptyMap()

    fun bindController(controller: Controller, robot: Robot) {
        controllerToRobots += controller to (controllerToRobots.getOrElse(controller, { emptySet() }) + robot)
    }

    fun unbindController(controller: Controller, robot: Robot) {
        controllerToRobots += controller to (controllerToRobots.getOrElse(controller, { emptySet() }) - robot)
        if (controllerToRobots[controller]?.isEmpty() == true) {
            controllerToRobots -= controller
        }
    }

    fun getBoundControllers(robot: Robot): Set<Controller> =
            controllerToRobots.filter { (_, robots) ->
                robots.contains(robot)
            }.keys

    fun getBoundRobots(controller: Controller): Set<Robot> =
            controllerToRobots.getOrElse(controller, { emptySet() })
}