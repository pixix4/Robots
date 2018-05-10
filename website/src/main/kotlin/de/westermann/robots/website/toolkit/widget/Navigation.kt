package de.westermann.robots.website.toolkit.widget

import de.westermann.robots.website.toolkit.Builder
import de.westermann.robots.website.toolkit.icon.Icon
import de.westermann.robots.website.toolkit.icon.MaterialIcon
import de.westermann.robots.website.toolkit.view.View
import de.westermann.robots.website.toolkit.view.ViewContainer
import kotlin.browser.window

/**
 * @author lars
 */

class Navigation(
        title: String,
        init: Navigation.() -> Unit
) : View() {

    val initRoute: String = window.location.pathname

    private val toolbar: Toolbar by ViewContainer(this, Toolbar::class) {
        Toolbar {
            icon = MaterialIcon.MENU
            iconAction { toggle() }
        }
    }


    private val navigationDrawer: NavigationDrawer by ViewContainer(this, NavigationDrawer::class) {
        NavigationDrawer {
            state.on {
                this@Navigation.element.classList.toggle("toggled", it)
            }
        }
    }

    fun toggle() = navigationDrawer.toggle()

    var content: View? by ViewContainer(this, "content") { null }

    fun route(route: String, name: String, icon: Icon, init: Builder.() -> Unit = {}) {
        var view: View? = null
        navigationDrawer.entry(name, icon) {
            if (view == null) {
                val builder = Builder()
                builder.init()
                view = builder.renderView()
            }
            content = view
            window.history.replaceState(null, name, route)
        }.also {
            if (route == initRoute) {
                navigationDrawer.select(it)
            }
        }
    }

    fun divider(title: String = "") {
        navigationDrawer.divider(title)
    }

    init {
        toolbar.title = title
        init()
    }
}

fun Builder.navigation(title: String, init: Navigation.() -> Unit = {}) = child(Navigation(title, init))
