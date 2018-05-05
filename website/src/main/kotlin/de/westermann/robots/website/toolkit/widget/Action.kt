package de.westermann.robots.website.toolkit.widget

import de.westermann.robots.website.toolkit.icon.Icon
import de.westermann.robots.website.toolkit.view.View
import de.westermann.robots.website.toolkit.view.ViewContainer

/**
 * @author lars
 */
class Action(text: String = "", icon: Icon? = null, init: Action.() -> Unit = {}) : View() {

    var iconView: IconView by ViewContainer(this, "icon") { IconView() }

    var text: TextView by ViewContainer(this, "text") { TextView() }

    init {
        this.text.text = text
        this.iconView.icon = icon
        init()
    }
}