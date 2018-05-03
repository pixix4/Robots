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

    protected abstract fun onCreate()

    fun addOnClickListener(listener: (Event) -> Unit) {
        clickListeners.add(listener)
    }

    fun action(listener: (Event) -> Unit) = addOnClickListener(listener)

    fun removeOnClickListener(listener: (Event) -> Unit) {
        clickListeners.remove(listener)
    }

    fun clearOnClickListener() {
        clickListeners.clear()
    }

    fun click(event: Event = Event("click")) {
        clickListeners.forEach { it.invoke(event) }
    }

    companion object {
        fun <T : View> create(view: T, postCreate: T.() -> Unit = {}): T {
            view.element.addClass(view::class.simpleName.toDashCase())
            view.element.addEventListener("click", view::click)
            view.onCreate()
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