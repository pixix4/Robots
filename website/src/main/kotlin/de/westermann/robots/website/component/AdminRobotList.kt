package de.westermann.robots.website.component

import de.westermann.robots.website.toolkit.Router
import de.westermann.robots.website.toolkit.view.ViewList

/**
 * @author lars
 */
class AdminRobotList : ViewList<RobotCard>() {
}

fun Router.adminRobotList() {
    view {
        println("adminRobotList")
        AdminRobotList()
    }
}