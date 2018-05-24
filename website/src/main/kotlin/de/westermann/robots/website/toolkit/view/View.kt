package de.westermann.robots.website.toolkit.view

import org.w3c.dom.HTMLElement
import org.w3c.dom.events.Event
import kotlin.browser.document
import kotlin.dom.addClass

/**
 * @author lars
 */

abstract class View(
        init: () -> Unit = {}
) {

    val click = TimeoutEventHandler<Event>()

    val element: HTMLElement = (document.createElement("div") as HTMLElement).also {
        it.addClass(this::class.simpleName.toDashCase())
        it.addEventListener("click", click::fire)
    }

    var visible: Boolean
        get() = element.style.display != "none"
        set(value) {
            element.style.display = if (value) "block" else "none"
        }

    fun offsetLeft(element: HTMLElement): Double = element.offsetLeft + ((element.parentElement as? HTMLElement)?.let {
        offsetLeft(it)
    } ?: 0.0)

    fun offsetTop(element: HTMLElement): Double = element.offsetTop + ((element.parentElement as? HTMLElement)?.let {
        offsetTop(it)
    } ?: 0.0)

    init {
        init()
    }
}

fun String?.toDashCase(): String = this?.run {
    replace("(.)([A-Z])".toRegex(), "$1-$2")
            .replace(" ", "-")
            .replace("-+".toRegex(), "-")
            .toLowerCase()
} ?: ""