package de.westermann.robots.website.component

import de.westermann.robots.datamodel.DeviceManager
import de.westermann.robots.datamodel.Robot
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
        (DeviceManager.controllers.find(listOf(search), 0.2, 10).map { it.element }.distinct() - robot.controllers).forEach { controller ->
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

    hook.on {
        search.requestFocus()
    }
}