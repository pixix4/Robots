package de.westermann.robots.website.toolkit.widget

import de.westermann.robots.website.toolkit.view.View
import de.westermann.robots.website.toolkit.view.ViewContainer
import de.westermann.robots.website.toolkit.view.ViewGroup

/**
 * @author lars
 */

class Toolbar private constructor() : ViewGroup() {

    private val actionContainer: ViewContainer<Toolbar, IconView> = ViewContainer(Toolbar::class, IconView::class)
    var action: IconView? by actionContainer

    private val titleContainer: ViewContainer<Toolbar, Text> = ViewContainer(Toolbar::class, Text::class)
    var title: Text? by titleContainer

    override fun onCreate() {
        setupContainer(actionContainer)
        setupContainer(titleContainer)
    }

    companion object {
        fun create(postCreate: Toolbar.() -> Unit): Toolbar = View.create(Toolbar(), postCreate)
    }
}