package de.westermann.robots.website.toolkit.widget

import de.westermann.robots.website.toolkit.view.ViewList

/**
 * @author lars
 */

class ActionList(init: ActionList.() -> Unit) : ViewList<Action>() {
    init {
        init()
    }
}