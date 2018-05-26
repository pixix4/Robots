package de.westermann.robots.website.toolkit.view

import de.westermann.robots.website.toolkit.widget.TextView
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import kotlin.browser.document
import kotlin.dom.clear

/**
 * @author lars
 */

open class ViewList<T : View> : View(), Iterable<T> {

    protected val children = mutableListOf<Pair<T, Element>>()

    private val footerElement = (document.createElement("div") as HTMLElement).also {
        it.classList.add("footer")
        element.appendChild(it)
    }
    var footer: View? = null
        set(value) {
            footerElement.clear()
            field = value

            if (value != null) {
                footerElement.appendChild(value.element)
            }
        }

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
            element.insertBefore(it.second, footerElement)
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
        children.map { it.first }.forEach(this::remove)
    }

    fun divider(text: String = "") {
        element.insertBefore(TextView(text).element, footerElement)
    }

    fun isEmpty() = children.isEmpty()

    protected open fun createContainer(): Element = document.createElement("div")

    override fun iterator(): Iterator<T> = children.map { it.first }.iterator()

    operator fun T.unaryPlus() {
        this@ViewList += this
    }
}