package de.westermann.robots.website.toolkit.widget

import de.westermann.robots.website.toolkit.view.View

/**
 * @author lars
 */

class TextView(text: String = "") : View() {

    var text: String
        get() = element.textContent ?: ""
        set(value) {
            element.textContent = value
        }

    init {
        this.text = text
    }
}