package de.westermann.robots.website.toolkit

import de.westermann.robots.website.toolkit.view.View
import org.w3c.dom.Node
import kotlin.dom.clear

fun render(node: Node, init: Builder.() -> Unit) {
    node.clear()
    val builder = Builder()
    builder.init()
    node.appendChild(builder.rootView.element)
}


class Builder {
    private val views = mutableListOf<View>()

    val viewList: List<View>
        get() = views.toList()

    val rootView: View
        get() = views.first()

    internal fun child(element: View) {
        views.add(element)
    }
}