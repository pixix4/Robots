package de.westermann.robots.website.toolkit.widget

import de.westermann.robots.website.toolkit.icon.Icon
import de.westermann.robots.website.toolkit.view.View
import de.westermann.robots.website.toolkit.view.ViewContainer
import de.westermann.robots.website.toolkit.view.toDashCase

/**
 * @author lars
 */

class Toolbar private constructor() : View() {
    private var iconView: IconView by ViewContainer(this, "icon") {
        IconView.create()
    }

    var icon: Icon?
        get() = iconView.icon
        set(value) {
            iconView.icon = value
        }

    private var titleView: TextView by ViewContainer(this, "title") {
        TextView.create()
    }

    var title: String?
        get() = titleView.text
        set(value) {
            titleView.text = value
        }

    fun iconAction(onAction: () -> Unit) {
        iconView.click.on { onAction() }
    }

    override val cssClasses: List<String> = super.cssClasses + Toolbar::class.simpleName.toDashCase()

    companion object {
        fun create(postCreate: Toolbar.() -> Unit): Toolbar = View.create(Toolbar(), postCreate)
    }
}