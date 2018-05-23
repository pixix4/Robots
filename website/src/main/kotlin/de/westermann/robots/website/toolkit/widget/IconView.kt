package de.westermann.robots.website.toolkit.widget

import de.westermann.robots.website.toolkit.icon.Icon
import de.westermann.robots.website.toolkit.view.View
import kotlin.dom.clear

/**
 * @author lars
 */

class IconView(icon: Icon? = null, init: IconView.() -> Unit = {}) : View() {

    var icon: Icon? = null
        set(value) {
            field = value

            element.clear()
            value?.let { element.appendChild(it.element) }
        }

    init {
        this.icon = icon
        element.setAttribute("aria-hidden", "true")
        init()
    }
}