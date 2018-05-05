package de.westermann.robots.website.toolkit.widget

import de.westermann.robots.website.toolkit.icon.Icon
import de.westermann.robots.website.toolkit.view.EventHandler
import de.westermann.robots.website.toolkit.view.SelectableViewList
import de.westermann.robots.website.toolkit.view.View
import kotlin.reflect.KClass

/**
 * @author lars
 */

class NavigationDrawer(init: NavigationDrawer.() -> Unit) : SelectableViewList<View>(false) {

    var first = true

    fun entry(name: String, icon: Icon, onSelect: () -> Unit) {
        val option = Action(name, icon)
        this += option
        bind(this, onSelect)
        if (first) {
            select(option)
            first = false
        }
    }

    val state = EventHandler<Boolean> {
        element.classList.toggle("toggled", it)
    }

    fun toggle() {
        state.fire(!element.classList.contains("toggled"))
    }

    fun divider(title: String = "") {
        this += TextView(title)
    }

    override val ignoreTypes: List<KClass<out View>> = super.ignoreTypes + TextView::class

    init {
        init()
    }
}