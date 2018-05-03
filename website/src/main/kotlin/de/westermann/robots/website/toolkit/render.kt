package de.westermann.robots.website.toolkit

import de.westermann.robots.website.toolkit.view.View
import org.w3c.dom.Node
import kotlin.dom.clear

fun render(node: Node, init: Builder.() -> Unit) = init(Builder(node))


class Builder internal constructor(
        private val root: Node
) {
    internal fun root(element: View) {
        root.appendChild(element.element)
    }

    init {
        root.clear()
    }
}