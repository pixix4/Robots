package de.westermann.robots.website.toolkit.widget

import de.westermann.robots.website.toolkit.view.View
import de.westermann.robots.website.toolkit.view.toDashCase

/**
 * @author lars
 */

class TextView private constructor() : View() {

    var text: String?
        get() = element.textContent
        set(value) {
            element.textContent = value
        }

    override val cssClasses: List<String> = super.cssClasses + TextView::class.simpleName.toDashCase()
    companion object {
        fun create(text: String? = null): TextView =
                View.create(TextView(), {
                    this.text = text
                })
    }
}