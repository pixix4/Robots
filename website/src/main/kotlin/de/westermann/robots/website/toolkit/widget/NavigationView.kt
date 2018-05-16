package de.westermann.robots.website.toolkit.widget

import de.westermann.robots.website.toolkit.icon.Icon
import de.westermann.robots.website.toolkit.icon.MaterialIcon
import de.westermann.robots.website.toolkit.view.View
import de.westermann.robots.website.toolkit.view.ViewContainer

/**
 * @author lars
 */

class NavigationView(
        title: String,
        init: NavigationView.() -> Unit
) : View() {

    private val toolbar: Toolbar by ViewContainer(this, Toolbar::class) {
        Toolbar {
            icon = MaterialIcon.MENU
            iconAction { toggle() }
        }
    }


    private val navigationDrawer: NavigationDrawer by ViewContainer(this, NavigationDrawer::class) {
        NavigationDrawer {
            state.on {
                this@NavigationView.element.classList.toggle("toggled", it)
            }
        }
    }

    fun toggle() = navigationDrawer.toggle()

    var content: View? by ViewContainer(this, "content") { null }

    fun entry(name: String, icon: Icon, onSelect: (View) -> Unit = {}): View = navigationDrawer.entry(name, icon, onSelect)

    fun select(elem: View, trigger:Boolean = true) = navigationDrawer.select(elem, trigger)

    fun divider(title: String = "") {
        navigationDrawer.divider(title)
    }

    init {
        toolbar.title = title
        init()
    }
}