package de.westermann.robots.website.toolkit.widget

import de.westermann.robots.datamodel.search.Searcher
import de.westermann.robots.website.toolkit.icon.Icon
import de.westermann.robots.website.toolkit.icon.MaterialIcon
import de.westermann.robots.website.toolkit.view.TimeoutEventHandler
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
            action.on {
                if (backButton) {
                    back.fire(Unit)
                } else {
                    toggle()
                }
            }

            search.on {
                val s = Searcher(it)
                println("Search for '$it'")
                println("    E: ${s.command}")
                println("    R: ${s.robots}")
                println("    C: ${s.controllers}")
            }
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

    fun entry(name: String, icon: Icon, onSelect: (Action) -> Unit = {}): Action = navigationDrawer.entry(name, icon, onSelect)

    fun select(elem: Action, trigger: Boolean = true) = navigationDrawer.select(elem, trigger)

    fun divider(title: String = "") {
        navigationDrawer.divider(title)
    }

    val back = TimeoutEventHandler<Unit>()

    var backButton: Boolean
        get() = toolbar.icon == MaterialIcon.ARROW_BACK
        set(value) {
            toolbar.icon = if (value) {
                MaterialIcon.ARROW_BACK
            } else {
                MaterialIcon.MENU
            }
        }

    init {
        toolbar.title = title
        init()
    }
}