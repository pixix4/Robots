package de.westermann.robots.website.toolkit.view

import org.w3c.dom.Element
import kotlin.browser.document
import kotlin.dom.clear

/**
 * @author lars
 */

open class ViewList<T : View> : View(), Iterable<T> {

    protected val children = mutableListOf<Pair<T, Element>>()

    operator fun get(index: Int): T = children[index].first

    operator fun set(index: Int, value: T) {
        children[index] = children[index].let {
            it.second.clear()
            it.second.appendChild(value.element)
            it.copy(first = value)
        }
    }

    fun add(child: T) {
        children.add(Pair(child, createContainer()).also {
            it.second.appendChild(child.element)
            element.appendChild(it.second)
        })
    }

    operator fun plusAssign(child: T) = add(child)

    fun remove(child: T) {
        children.find { it.first == child }?.also {
            children.remove(it)
            element.removeChild(it.second)
            it.second.clear()
        }
    }

    operator fun minusAssign(child: T) = remove(child)

    fun clear() {
        children.forEach {
            remove(it.first)
        }
    }

    fun isEmpty() = children.isEmpty()

    protected open fun createContainer(): Element = document.createElement("div")

    override fun iterator(): Iterator<T> = children.map { it.first }.iterator()

}