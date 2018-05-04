package de.westermann.robots.website.toolkit.widget

import de.westermann.robots.website.toolkit.icon.Icon
import de.westermann.robots.website.toolkit.view.View
import de.westermann.robots.website.toolkit.view.toDashCase
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

    override val cssClasses: List<String> = super.cssClasses + IconView::class.simpleName.toDashCase()
    companion object {
        fun create(iconName: Icon? = null): IconView =
                View.create(IconView(), {
                    icon = iconName
                })
    }
}