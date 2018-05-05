package de.westermann.robots.website.toolkit.view

import org.w3c.dom.Element
import kotlin.browser.document
import kotlin.dom.clear

/**
 * @author lars
 */

abstract class ViewList<T : View> : View(), Iterable<T> {

    protected val children = mutableListOf<Pair<T, Element>>()

    operator fun get(index: Int): T = children[index].first

    operator fun set(index: Int, value: T) {
        children[index] = children[index].let {
            it.second.clear()
            it.second.appendChild(value.element)
            it.copy(first = value)
        }
    }

    operator fun plusAssign(child: T) {
        children.add(Pair(child, createContainer()).also {
            it.second.appendChild(child.element)
            element.appendChild(it.second)
        })
    }

    operator fun minusAssign(child: T) {
        children.find { it.first == child }?.also {
            children.remove(it)
            element.removeChild(it.second)
            it.second.clear()
        }
    }

    protected open fun createContainer(): Element = document.createElement("div")

    override fun iterator(): Iterator<T> = children.map { it.first }.iterator()

}