package de.westermann.robots.website.toolkit.widget

import de.westermann.robots.website.toolkit.icon.Icon
import de.westermann.robots.website.toolkit.view.EventHandler
import de.westermann.robots.website.toolkit.view.SelectableViewList
import de.westermann.robots.website.toolkit.view.View
import de.westermann.robots.website.toolkit.view.toDashCase
import kotlin.reflect.KClass

/**
 * @author lars
 */

class NavigationDrawer private constructor() : SelectableViewList<View>(false) {

    var first = true

    override val cssClasses: List<String> = super.cssClasses + NavigationDrawer::class.simpleName.toDashCase()

    fun entry(name: String, icon: Icon, onSelect: () -> Unit) {
        val option = Option.create(name, icon)
        this += option
        bind(this, onSelect)
        if (first) {
            select(option)
            first = false
        }
    }

    fun divider(title: String? = null) {
        this += TextView.create(title)
    }

    override val ignoreTypes: List<KClass<out View>> = super.ignoreTypes + TextView::class

    companion object {
        fun create(postCreate: NavigationDrawer.() -> Unit): NavigationDrawer =
                View.create(NavigationDrawer(), postCreate)

    }
}