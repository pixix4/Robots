package de.westermann.robots.website.toolkit.view

import org.w3c.dom.Element
import kotlin.browser.document
import kotlin.dom.addClass
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

/**
 * @author lars
 */
class ViewContainer<in Container : ViewGroup, Child : View>(containerClass: KClass<Container>, childClass: KClass<Child>) {
    private var content: Child? = null
    private val container: Element = document.createElement("div")

    init {
        container.addClass(containerClass.simpleName.toDashCase() + "-" + childClass.simpleName.toDashCase())
    }

    operator fun getValue(gToolbar: Container, property: KProperty<*>): Child? {
        return content
    }

    operator fun setValue(gToolbar: Container, property: KProperty<*>, value: Child?) {
        content?.let { container.removeChild(it.element) }
        content = value
        content?.let { container.appendChild(it.element) }
    }

    fun putToElement(parent: Element) {
        parent.appendChild(container)
    }
}