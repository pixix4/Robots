package de.westermann.robots.website.component

import de.westermann.robots.website.toolkit.Router
import de.westermann.robots.website.toolkit.view.View

/**
 * @author lars
 */
class AdminOverview:View() {
}
fun Router.adminOverview() {
    view {
        println("adminOverview")
        AdminOverview()
    }
}