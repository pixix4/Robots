package de.westermann.robots.website.toolkit.widget

import de.westermann.robots.website.toolkit.view.View
import de.westermann.robots.website.toolkit.view.ViewGroup

/**
 * @author lars
 */

class NavigationDrawer private constructor() : ViewGroup() {

    override fun onCreate() {}

    companion object {
        fun create(postCreate: NavigationDrawer.() -> Unit): NavigationDrawer =
                View.create(NavigationDrawer(), postCreate)

    }
}