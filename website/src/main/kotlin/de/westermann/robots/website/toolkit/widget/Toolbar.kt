package de.westermann.robots.website.toolkit.widget

import de.westermann.robots.website.toolkit.icon.Icon
import de.westermann.robots.website.toolkit.icon.MaterialIcon
import de.westermann.robots.website.toolkit.view.View
import de.westermann.robots.website.toolkit.view.ViewContainer

/**
 * @author lars
 */

class Toolbar(
        init: Toolbar.() -> Unit
) : View() {

    private val iconView: IconView by ViewContainer(this, "icon") {
        IconView()
    }

    var icon: Icon?
        get() = iconView.icon
        set(value) {
            iconView.icon = value
        }

    private val titleView: TextView by ViewContainer(this, "title") {
        TextView()
    }

    var title: String
        get() = titleView.text
        set(value) {
            titleView.text = value
        }

    fun iconAction(onAction: () -> Unit) {
        iconView.click.on { onAction() }
    }

    private val searchBar: Input by ViewContainer(this, "search") {
        Input {
            placeholder = "Search…"
            icon = MaterialIcon.SEARCH
        }
    }

    var enableSearchBar: Boolean
        get() = searchBar.visible
        set(value) {
            searchBar.visible = value
        }

    private val searchIcon: IconView by ViewContainer(this, "search-icon") {
        IconView(MaterialIcon.SEARCH) {
            click.on {
                searchBar.element.classList.add("active")
            }
        }
    }

    init {
        init()
    }
}