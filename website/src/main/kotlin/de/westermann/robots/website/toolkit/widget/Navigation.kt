package de.westermann.robots.website.toolkit.widget

import de.westermann.robots.website.toolkit.Builder
import de.westermann.robots.website.toolkit.icon.Icon
import de.westermann.robots.website.toolkit.view.View
import de.westermann.robots.website.toolkit.view.ViewContainer
import de.westermann.robots.website.toolkit.view.ViewGroup

/**
 * @author lars
 */

class Navigation private constructor() : ViewGroup() {


    private val toolbarContainer = ViewContainer(Navigation::class, Toolbar::class)
    var toolbar: Toolbar? by toolbarContainer

    private val navigationDrawerContainer = ViewContainer(Navigation::class, NavigationDrawer::class)
    var navigationDrawer: NavigationDrawer? by navigationDrawerContainer

    private val contentContainer = ViewContainer(Navigation::class, ViewGroup::class)
    var content: ViewGroup? by contentContainer

    override fun onCreate() {
        setupContainer(toolbarContainer)
        setupContainer(navigationDrawerContainer)
        setupContainer(contentContainer)

        toolbar = Toolbar.create {

        }

        navigationDrawer = NavigationDrawer.create {

        }
    }

    fun route(route: String, name: String, icon: Icon, init: ViewGroup.() -> Unit = {}) {
        toolbar
    }

    fun divider(name: String = "") {

    }

    companion object {
        fun create(postCreate: Navigation.() -> Unit): Navigation = View.create(Navigation(), postCreate)
    }
}

fun Builder.navigation(title: String, init: Navigation.() -> Unit = {}) = root(Navigation.create(init))
