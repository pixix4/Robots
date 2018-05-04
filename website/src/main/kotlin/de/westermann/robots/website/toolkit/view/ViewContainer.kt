package de.westermann.robots.website.toolkit.view

import org.w3c.dom.Element
import kotlin.browser.document
import kotlin.dom.addClass
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

/**
 * @author lars
 */
class ViewContainer<in Container : View, Child : View?>(
        parentClass: Container,
        fieldName: String,
        init: () -> Child
) {

    constructor(
            containerClass: Container,
            childClass: KClass<out View>,
            init: () -> Child
    ) : this(
            containerClass,
            childClass.simpleName.toDashCase(),
            init
    )

    private val container: Element = document.createElement("div").also {
        it.addClass(parentClass::class.simpleName.toDashCase() + "-$fieldName")
    }
    private var content: Child = init().also {
        it?.element?.let { this.container.appendChild(it) }
    }

    operator fun getValue(container: Container, property: KProperty<*>): Child {
        return content
    }

    operator fun setValue(container: Container, property: KProperty<*>, value: Child) {
        content?.element?.let { this.container.removeChild(it) }
        content = value
        content?.element?.let { this.container.appendChild(it) }
    }


    init {
        parentClass.element.appendChild(container)
    }
}