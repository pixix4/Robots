package de.westermann.robots.website.toolkit.widget

import de.westermann.robots.website.toolkit.icon.Icon
import de.westermann.robots.website.toolkit.view.View
import de.westermann.robots.website.toolkit.view.ViewContainer
import de.westermann.robots.website.toolkit.view.toDashCase

/**
 * @author lars
 */
class Option private constructor() : View() {

    var iconView: IconView by ViewContainer(this, "icon") {
        IconView.create()
    }

    var text: TextView by ViewContainer(this, "text") {
        TextView.create()
    }

    override val cssClasses: List<String> = super.cssClasses + Option::class.simpleName.toDashCase()

    companion object {
        fun create(text: String? = null, icon: Icon? = null, postCreate: Option.() -> Unit = {}): Option =
                View.create(Option()) {
                    this.iconView = IconView.create(icon)
                    this.text = TextView.create(text)
                    postCreate(this)
                }
    }
}