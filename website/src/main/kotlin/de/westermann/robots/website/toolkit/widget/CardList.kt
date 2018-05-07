package de.westermann.robots.website.toolkit.widget

import de.westermann.robots.website.toolkit.view.ViewList

/**
 * @author lars
 */
class CardList(init: CardList.() -> Unit): ViewList<Card>() {
    init {
        init()
    }
}