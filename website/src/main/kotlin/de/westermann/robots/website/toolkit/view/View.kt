package de.westermann.robots.website.toolkit.view

import org.w3c.dom.events.Event
import kotlin.browser.document
import kotlin.dom.addClass

/**
 * @author lars
 */

abstract class View {

    val element = document.createElement("div")
    private val clickListeners = mutableListOf<(Event) -> Unit>()

    fun action(listener: (Event) -> Unit) {
        clickListeners.add(listener)
    }

    fun click(event: Event = Event("click")) {
        clickListeners.forEach { it.invoke(event) }
    }

    open val cssClasses: List<String> = listOf(View::class.simpleName.toDashCase())

    companion object {
        fun <T : View> create(view: T, postCreate: T.() -> Unit = {}): T {
            view.element.addClass(*view.cssClasses.toTypedArray())
            view.element.addEventListener("click", view::click)
            postCreate(view)
            return view
        }
    }
}

fun String?.toDashCase(): String = this?.run {
    replace("(.)([A-Z])".toRegex(), "$1-$2")
            .replace(" ", "-")
            .replace("-+".toRegex(), "-")
            .toLowerCase()
} ?: ""