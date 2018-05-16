package de.westermann.robots.website.toolkit.widget

import de.westermann.robots.website.toolkit.view.View
import de.westermann.robots.website.toolkit.view.ViewList

/**
 * @author lars
 */
class CardView<T : View>(init: CardView<T>.() -> Unit) : ViewList<T>() {
    init {
        init()
    }
}
