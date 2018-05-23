package de.westermann.robots.datamodel

import de.westermann.robots.datamodel.observe.Library

/**
 * @author lars
 */
object DeviceManager {
    val robots = Library<Robot>().also {
        it.onChange(object : Library.Observer<Robot> {
            override fun onRemove(element: Robot) {
                controllerToRobots.filter { it.value.contains(element) }.keys.forEach {
                    unbindController(it, element)
                }
            }
        })
    }
    val controllers = Library<Controller>().also {
        it.onChange(object : Library.Observer<Controller> {
            override fun onRemove(element: Controller) {
                controllerToRobots[element]?.forEach {
                    unbindController(element, it)
                }
            }
        })
    }

    private var controllerToRobots: Map<Controller, Set<Robot>> = emptyMap()

    fun bindController(controller: Controller, robot: Robot) {
        val old = controllerToRobots
        controllerToRobots += controller to (controllerToRobots.getOrElse(controller, { emptySet() }) + robot)
        if (old != controllerToRobots) {
            notifyOnBind(controller, robot)
            controller.robotsProperty.update()
            robot.controllersProperty.update()
        }
    }

    fun unbindController(controller: Controller, robot: Robot) {
        val old = controllerToRobots
        controllerToRobots += controller to (controllerToRobots.getOrElse(controller, { emptySet() }) - robot)
        if (controllerToRobots[controller]?.isEmpty() == true) {
            controllerToRobots -= controller
        }
        if (old != controllerToRobots) {
            notifyOnUnbind(controller, robot)
            controller.robotsProperty.update()
            robot.controllersProperty.update()
        }
    }

    fun getBoundControllers(robot: Robot): Set<Controller> =
            controllerToRobots.filter { (_, robots) ->
                robots.contains(robot)
            }.keys

    fun getBoundRobots(controller: Controller): Set<Robot> =
            controllerToRobots.getOrElse(controller, { emptySet() })

    private fun notifyOnBind(controller: Controller, robot: Robot) {
        bindChangeListeners.forEach { it.onBind(controller, robot) }
    }

    private fun notifyOnUnbind(controller: Controller, robot: Robot) {
        bindChangeListeners.forEach { it.onUnbind(controller, robot) }
    }

    var bindChangeListeners = emptyList<OnBindChange>()
    fun onBindChange(onBindChange: OnBindChange) {
        bindChangeListeners += onBindChange
    }

    fun removeOnBindChange(onBindChange: OnBindChange) {
        bindChangeListeners -= onBindChange
    }

    interface OnBindChange {
        fun onBind(controller: Controller, robot: Robot)
        fun onUnbind(controller: Controller, robot: Robot)
    }
}