package de.westermann.robots.website.component

import de.westermann.robots.website.WebSocketConnection
import de.westermann.robots.website.toolkit.Router
import de.westermann.robots.website.toolkit.view.View
import de.westermann.robots.website.toolkit.view.ViewContainer
import de.westermann.robots.website.toolkit.widget.Input

/**
 * @author lars
 */
class AdminLogin: View() {

    private val password: Input by ViewContainer(this, "password") {
        Input {
            placeholder = "Password"
            type = Input.Type.PASSWORD
            submit.on {
                WebSocketConnection.iServer.login(it)
            }
        }
    }
}
fun Router.adminLogin() {
    view {
        AdminLogin()
    }
}