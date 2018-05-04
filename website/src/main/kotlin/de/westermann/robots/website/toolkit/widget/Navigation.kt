package de.westermann.robots.website.toolkit.widget

import de.westermann.robots.website.toolkit.Builder
import de.westermann.robots.website.toolkit.icon.Icon
import de.westermann.robots.website.toolkit.icon.MaterialIcon
import de.westermann.robots.website.toolkit.view.View
import de.westermann.robots.website.toolkit.view.ViewContainer
import de.westermann.robots.website.toolkit.view.toDashCase

/**
 * @author lars
 */

class Navigation private constructor() : View() {


    var toolbar: Toolbar by ViewContainer(this, Toolbar::class) {
        Toolbar.create {
            icon = MaterialIcon.MENU
        }
    }

    var navigationDrawer: NavigationDrawer by ViewContainer(this, NavigationDrawer::class) {
        NavigationDrawer.create { }
    }

    var content: View? by ViewContainer(this, "content") { null }

    fun route(route: String, name: String, icon: Icon, init: Builder.() -> Unit = {}) {
        navigationDrawer.entry(name, icon) {
            val builder = Builder()
            builder.init()
            content = builder.rootView
        }
    }

    fun divider(title: String = "") {
        navigationDrawer.divider(title)
    }

    override val cssClasses: List<String> = super.cssClasses + Navigation::class.simpleName.toDashCase()

    companion object {
        fun create(title: String, postCreate: Navigation.() -> Unit): Navigation = View.create(Navigation(), {
            toolbar.title = title
            postCreate()
        })
    }
}

fun Builder.navigation(title: String, init: Navigation.() -> Unit = {}) = child(Navigation.create(title, init))
