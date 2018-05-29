package de.westermann.robots.website.toolkit.view

import org.w3c.dom.DOMTokenList
import org.w3c.dom.Element

/**
 * @author lars
 */
class CssClasses(
        element: Element
) {
    private val list: DOMTokenList = element.classList

    operator fun plusAssign(cssClass: String) {
        list.add(cssClass)
    }

    operator fun minusAssign(cssClass: String) {
        list.remove(cssClass)
    }

    fun toggle(cssClass: String) {
        list.toggle(cssClass)
    }

    fun toggle(cssClass: String, force: Boolean) {
        list.toggle(cssClass, force)
    }

    operator fun set(cssClass: String, value: Boolean) {
        toggle(cssClass, value)
    }

    operator fun get(cssClass: String): Boolean {
        return list.contains(cssClass)
    }
}