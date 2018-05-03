package de.westermann.robots.website.toolkit.widget

import de.westermann.robots.website.toolkit.icon.Icon
import de.westermann.robots.website.toolkit.view.View
import kotlin.dom.clear

/**
 * @author lars
 */

class IconView private constructor() : View() {

    var icon: Icon? = null
        set(value) {
            field = value

            element.clear()
            value?.let { element.appendChild(it.element) }
        }

    override fun onCreate() {
        //Nothing to do
    }

    companion object {
        fun create(iconName: Icon? = null): IconView =
                View.create(IconView(), {
                    icon = iconName
                })
    }
}