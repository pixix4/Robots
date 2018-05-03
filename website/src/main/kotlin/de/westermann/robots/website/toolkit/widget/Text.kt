package de.westermann.robots.website.toolkit.widget

import de.westermann.robots.website.toolkit.view.View

/**
 * @author lars
 */

class Text private constructor() : View() {

    var text: String?
        get() = element.textContent
        set(value) {
            element.textContent = value
        }

    override fun onCreate() {
        //Nothing to do
    }

    companion object {
        fun create(text: String? = null): Text =
                View.create(Text(), {
                    this.text = text
                })
    }
}