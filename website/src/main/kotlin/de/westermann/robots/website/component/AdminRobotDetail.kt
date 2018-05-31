package de.westermann.robots.website.component

import de.westermann.robots.datamodel.DeviceManager
import de.westermann.robots.website.toolkit.Router
import de.westermann.robots.website.toolkit.view.View

/**
 * @author lars
 */
class AdminRobotDetail(
        val id: Int
) : View()

fun Router.adminRobotDetail(id: Int) {
    view {
        DeviceManager.robots[id]?.let {
            RobotDetail(it)
        } ?: AdminRobotDetail(id)
    }
}