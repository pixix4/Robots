package de.westermann.robots.website.toolkit.widget

import de.westermann.robots.website.toolkit.view.View
import de.westermann.robots.website.toolkit.view.ViewList

/**
 * @author lars
 */
class CardView<T : View>(init: CardView<T>.() -> Unit) : ViewList<T>() {

    var hoverHighlight: Boolean
        get() = !element.classList.contains("no-highlight")
        set(value) {
            element.classList.toggle("no-highlight", !value)
        }

    init {
        init()
    }
}
