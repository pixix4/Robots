package de.westermann.robots.website.toolkit.widget

import de.westermann.robots.website.toolkit.Router
import de.westermann.robots.website.toolkit.view.View
import de.westermann.robots.website.toolkit.view.ViewList

/**
 * @author lars
 */
class Box(init: Box.() -> Unit): ViewList<View>() {
    init {
        init()
    }
}

fun ViewList<View>.box(init: Box.() -> Unit) {
    this += Box(init)
}

fun Router.box(init: Box.() -> Unit) {
    view {
        Box(init)
    }
}