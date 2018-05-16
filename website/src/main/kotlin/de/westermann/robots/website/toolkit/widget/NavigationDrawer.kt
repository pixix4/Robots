package de.westermann.robots.website.toolkit.widget

import de.westermann.robots.website.toolkit.icon.Icon
import de.westermann.robots.website.toolkit.view.EventHandler
import de.westermann.robots.website.toolkit.view.SelectableViewList
import de.westermann.robots.website.toolkit.view.View
import kotlin.reflect.KClass

/**
 * @author lars
 */

class NavigationDrawer(init: NavigationDrawer.() -> Unit) : SelectableViewList<Action>(false) {

    fun entry(name: String, icon: Icon, onSelect: (Action) -> Unit): Action {
        val action = Action(name, icon)
        this += action
        bind(action, onSelect)
        return action
    }

    val state = EventHandler<Boolean> {
        element.classList.toggle("toggled", it)
    }

    fun toggle() {
        state.fire(!element.classList.contains("toggled"))
    }

    init {
        init()
    }
}