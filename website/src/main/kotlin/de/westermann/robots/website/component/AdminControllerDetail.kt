package de.westermann.robots.website.component

import de.westermann.robots.website.toolkit.Router
import de.westermann.robots.website.toolkit.view.View

/**
 * @author lars
 */
class AdminControllerDetail(
    val id: Int
): View() {
}
fun Router.adminControllerDetail(id: Int) {
    view {
        println("adminControllerDetail $id")
        AdminControllerDetail(id)
    }
}