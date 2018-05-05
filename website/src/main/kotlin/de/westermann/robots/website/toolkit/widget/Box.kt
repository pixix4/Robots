package de.westermann.robots.website.toolkit.widget

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