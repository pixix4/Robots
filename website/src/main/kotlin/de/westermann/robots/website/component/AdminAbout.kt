package de.westermann.robots.website.component

import de.westermann.robots.website.toolkit.Router
import de.westermann.robots.website.toolkit.view.View

/**
 * @author lars
 */
class AdminAbout: View() {
}
fun Router.adminAbout() {
    view {
        println("adminAbout")
        AdminAbout()
    }
}