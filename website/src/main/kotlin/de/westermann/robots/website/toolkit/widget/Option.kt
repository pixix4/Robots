package de.westermann.robots.website.toolkit.widget

import de.westermann.robots.website.toolkit.icon.Icon
import de.westermann.robots.website.toolkit.view.View
import de.westermann.robots.website.toolkit.view.ViewContainer
import de.westermann.robots.website.toolkit.view.ViewGroup

/**
 * @author lars
 */
class Option private constructor() : ViewGroup() {

    private val iconViewContainer = ViewContainer(Option::class, IconView::class)
    var iconView: IconView? by iconViewContainer

    private val textContainer = ViewContainer(Option::class, Text::class)
    var text: Text? by textContainer

    override fun onCreate() {
        setupContainer(iconViewContainer)
        setupContainer(textContainer)
    }

    companion object {
        fun create(text: String? = null, icon: Icon? = null, postCreate: Option.() -> Unit = {}): Option =
                View.create(Option()) {
                    this.iconView = IconView.create(icon)
                    this.text = Text.create(text)
                    postCreate(this)
                }
    }
}