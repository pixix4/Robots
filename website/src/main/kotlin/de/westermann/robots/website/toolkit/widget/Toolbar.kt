package de.westermann.robots.website.toolkit.widget

import de.westermann.robots.website.toolkit.icon.Icon
import de.westermann.robots.website.toolkit.icon.MaterialIcon
import de.westermann.robots.website.toolkit.view.TimeoutEventHandler
import de.westermann.robots.website.toolkit.view.View
import de.westermann.robots.website.toolkit.view.ViewContainer

/**
 * @author lars
 */

class Toolbar(
        init: Toolbar.() -> Unit
) : View() {

    private val iconView: IconView by ViewContainer(this, "icon") {
        IconView {
            click.on {
                if (searchMode) {
                    searchMode = false
                } else {
                    action.fire(Unit)
                }
            }
        }
    }

    var icon: Icon? = null
        set(value) {
            field = value
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

    val action = TimeoutEventHandler<Unit>()

    private val searchBar: Input by ViewContainer(this, "search") {
        Input {
            placeholder = "Search…"
            icon = MaterialIcon.SEARCH
            focus.on {
                searchMode = true
            }
            click.on {
                searchMode = true
            }
            exit.on {
                searchMode = false
            }
        }
    }

    private var searchMode: Boolean
        get() = element.classList.contains("search")
        set(value) {
            element.classList.toggle("search", value)

            if (value) {
                iconView.icon = MaterialIcon.CLOSE
            } else {
                iconView.icon = icon
            }
        }

    private val searchIcon: IconView by ViewContainer(this, "search-icon") {
        IconView(MaterialIcon.SEARCH) {
            click.on {
                searchMode = true
            }
        }
    }

    init {
        init()
    }
}