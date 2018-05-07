package de.westermann.robots.website.toolkit.widget

import de.westermann.robots.website.toolkit.view.View
import de.westermann.robots.website.toolkit.view.ViewList

/**
 * @author lars
 */
open class Card(init: Card.() -> Unit): ViewList<View>() {
    init {
        init()
    }
}