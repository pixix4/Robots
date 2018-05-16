package de.westermann.robots.website.component

import de.westermann.robots.website.toolkit.Router
import de.westermann.robots.website.toolkit.view.View

/**
 * @author lars
 */
class AdminLogin: View() {
}
fun Router.adminLogin() {
    view {
        println("adminLogin")
        AdminLogin()
    }
}