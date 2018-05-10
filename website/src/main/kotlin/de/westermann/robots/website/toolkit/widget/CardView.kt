package de.westermann.robots.website.toolkit.widget

import de.westermann.robots.website.toolkit.Builder
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


fun Builder.cardView(init: CardView.() -> Unit = {}) = child(CardView(init))
