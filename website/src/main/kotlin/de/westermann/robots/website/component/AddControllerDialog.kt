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
fun addControllerDialog(robot: Robot) = Dialog {
    title = "Add controller"

    val list = ViewList<ControllerListItem>()

    fun update(search: String = "") {
        list.clear()
        (DeviceManager.controllers.find(listOf(search), 0.0, 10).asSequence().map { it.element }.distinct().toList() - robot.controllers).forEach { controller ->
            list += ControllerListItem(controller, robot) {
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
        override fun onAdd(element: Controller) {
            update(search.value)
        }

        override fun onChange(element: Controller) {
            update(search.value)
        }

        override fun onRemove(element: Controller) {
            update(search.value)
        }
    }
    val robotListener = object : Library.Observer<Robot> {
        override fun onRemove(element: Robot) {
            if (element == robot) {
                hide()
            }
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
