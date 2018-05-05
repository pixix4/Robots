package de.westermann.robots.website.toolkit

import de.westermann.robots.website.toolkit.view.View
import de.westermann.robots.website.toolkit.widget.Box
import org.w3c.dom.Node
import kotlin.dom.clear

fun render(node: Node, init: Builder.() -> Unit) {
    node.clear()
    val builder = Builder()
    builder.init()
    builder.renderToNode(node)
}


class Builder {
    private val views = mutableListOf<View>()

    fun renderToNode(node: Node) {
        node.clear()
        views.forEach { node.appendChild(it.element) }
    }

    fun renderView(): View = if (views.size == 1) {
        views.first()
    } else {
        Box { views.forEach { this += it } }
    }


    internal fun child(element: View) {
        views.add(element)
    }
}