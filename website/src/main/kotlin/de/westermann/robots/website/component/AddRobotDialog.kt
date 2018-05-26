package de.westermann.robots.website.component

import de.westermann.robots.datamodel.Controller
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

    content = Box {
        +Input("") {
            placeholder = "Search…"
            change.on(::update)
        }
        +list
    }
    update()
}