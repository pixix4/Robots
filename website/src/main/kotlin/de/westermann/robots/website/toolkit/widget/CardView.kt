package de.westermann.robots.website.toolkit.widget

import de.westermann.robots.website.toolkit.Router
import de.westermann.robots.website.toolkit.view.View
import de.westermann.robots.website.toolkit.view.ViewList

/**
 * @author lars
 */
class CardView(init: CardView.() -> Unit) : ViewList<View>() {
    init {
        init()
    }
}


fun Router.cardView(init: CardView.() -> Unit = {}) = view { CardView(init) }
