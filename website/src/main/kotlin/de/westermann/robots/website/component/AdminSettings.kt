package de.westermann.robots.website.component

import de.westermann.robots.website.toolkit.Router
import de.westermann.robots.website.toolkit.view.View

/**
 * @author lars
 */
class AdminSettings : View() {
}

fun Router.adminSettings() {
    view {
        println("adminSettings")
        AdminSettings()
    }
}