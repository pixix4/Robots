package de.westermann.robots.website.toolkit

import de.westermann.robots.website.toolkit.icon.Icon
import de.westermann.robots.website.toolkit.widget.Box
import de.westermann.robots.website.toolkit.widget.NavigationView

/**
 * @author lars
 */
class Navigation(
        parent: Router?,
        title: String
) : Router(parent) {

    private val inner = Box {}
    private val nav = NavigationView(title) {
        content = inner
        back.on {
            Router.routeUp()
        }
    }.also {
        view { it }
    }

    fun route(route: String, name: String, icon: Icon, init: Router.() -> Unit) {
        route(route) {
            val action = nav.entry(name, icon) {
                Router.routeTo(fullPath)
            }
            onRender {
                nav.select(action, false)
            }
            init()
        }
    }

    fun route(name: String, icon: Icon, init: Router.() -> Unit) = route("", name, icon, init)

    fun divider(name: String = "") {
        nav.divider(name)
    }

    override val rootView = inner.element
}

fun Router.navigation(title: String, init: Navigation.() -> Unit) {
    child(Navigation(this, title)).init()
}