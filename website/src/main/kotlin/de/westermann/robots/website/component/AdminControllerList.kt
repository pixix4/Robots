package de.westermann.robots.website.component

import de.westermann.robots.website.toolkit.Router
import de.westermann.robots.website.toolkit.view.ViewList

/**
 * @author lars
 */
class AdminControllerList: ViewList<ControllerCard>() {
}
fun Router.adminControllerList() {
    view {
        println("adminControllerList")
        AdminControllerList()
    }
}