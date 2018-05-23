package de.westermann.robots.website.toolkit.widget

import de.westermann.robots.website.toolkit.view.View
import de.westermann.robots.website.toolkit.view.ViewList

/**
 * @author lars
 */

class TextView(text: String = "", placeholder: String = "") : View() {

    var text: String = text
        set(value) {
            field = value
            update()
        }

    var placeholder: String = placeholder
        set(value) {
            field = value
            update()
        }

    private fun update() {
        if (text.isEmpty()) {
            element.textContent = placeholder
            element.classList.add("placeholder")
        } else {
            element.textContent = text
            element.classList.remove("placeholder")
        }
    }

    init {
        this.text = text
    }
}

fun ViewList<View>.textView(text: String = "", placeholder: String = "") {
    this += TextView(text, placeholder)
}