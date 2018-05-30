package de.westermann.robots.website.component

import de.westermann.robots.datamodel.Controller
import de.westermann.robots.datamodel.DeviceManager
import de.westermann.robots.datamodel.Robot
import de.westermann.robots.datamodel.observe.Library
import de.westermann.robots.website.WebSocketConnection
import de.westermann.robots.website.toolkit.view.ViewList
import de.westermann.robots.website.toolkit.widget.Box
import de.westermann.robots.website.toolkit.widget.Dialog
import de.westermann.robots.website.toolkit.widget.Input

/**
 * @author lars
 */
fun addRobotDialog(controller: Controller) = Dialog {
    title = "Add robot"

    val list = ViewList<RobotListItem>()

    fun update(search: String = "") {
        list.clear()
        (DeviceManager.robots.find(listOf(search), 0.2, 10).map { it.element }.distinct() - controller.robots).forEach { robot ->
            list += RobotListItem(robot, controller) {
                WebSocketConnection.iServer.bind(controller.id, robot.id)
                hide()
                true
            }
        }
    }

    val search = Input("") {
        placeholder = "Search…"
        change.on(::update)
    }

    content = Box {
        +search
        +list
    }
    update()


    val controllerListener = object : Library.Observer<Controller> {
        override fun onRemove(element: Controller) {
            if (element == controller) {
                hide()
            }
        }
    }
    val robotListener = object : Library.Observer<Robot> {
        override fun onAdd(element: Robot) {
            update(search.value)
        }

        override fun onChange(element: Robot) {
            update(search.value)
        }

        override fun onRemove(element: Robot) {
            update(search.value)
        }
    }

    DeviceManager.robots.onChange(robotListener)
    DeviceManager.controllers.onChange(controllerListener)

    close.on {
        DeviceManager.robots.removeObserver(robotListener)
        DeviceManager.controllers.removeObserver(controllerListener)
    }

    open.on {
        search.requestFocus()
    }
}