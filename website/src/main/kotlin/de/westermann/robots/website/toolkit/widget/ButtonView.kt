package de.westermann.robots.website.toolkit.widget

import de.westermann.robots.website.toolkit.Router
import de.westermann.robots.website.toolkit.view.*
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventListener
import org.w3c.dom.events.MouseEvent
import kotlin.browser.document
import kotlin.browser.window

/**
 * @author lars
 */
class ButtonView(
        text: String,
        init: ButtonView.() -> Unit = {}
) : View() {

    private val button = (document.createElement("div") as HTMLElement).also {
        element.appendChild(it)
    }

    val down = TimeoutEventHandler<Unit>()
    val up = TimeoutEventHandler<Unit>()

    var text: String = ""
        set(value) {
            field = value
            println("astef: $value")
            button.textContent = value
        }

    var disable: Boolean
        get() = element.classList.contains("disabled")
        set(value) {
            element.classList.toggle("disabled", value)
        }

    private val stopListener = object : EventListener {
        override fun handleEvent(event: Event) {
            stopClick(event)
        }
    }

    private var identifier: Int? = null

    private fun startClick(event: Event) {
        event.preventDefault()
        event.stopPropagation()

        if (identifier != null || disable) return

        if (jsTypeOf(js("TouchEvent")) != "undefined") {
            (event as? TouchEvent)?.let {
                val touch = it.changedTouches[0] ?: return
                identifier = touch.identifier
                window.addEventListener("touchend", stopListener)
                window.addEventListener("touchcancel", stopListener)
                down.fire(Unit)
            }
            return
        }

        (event as? MouseEvent)?.let {
            identifier = -1
            window.addEventListener("mouseup", stopListener)
            down.fire(Unit)
        }

    }

    private fun stopClick(event: Event) {
        event.preventDefault()
        event.stopPropagation()

        if (jsTypeOf(js("TouchEvent")) != "undefined") {
            (event as? TouchEvent)?.let {
                it.changedTouches.find(identifier ?: -1) ?: return
                window.removeEventListener("touchend", stopListener)
                window.removeEventListener("touchcancel", stopListener)
                identifier = null
                up.fire(Unit)
            }
            return
        }
        (event as? MouseEvent)?.let {
            window.removeEventListener("mouseup", stopListener)
            identifier = null
            up.fire(Unit)
        }
    }

    init {
        element.addEventListener("mousedown", this::startClick)
        element.addEventListener("touchstart", this::startClick)
        element.addEventListener("contextmenu", object : EventListener {
            override fun handleEvent(event: Event) {
                event.stopPropagation()
                event.preventDefault()
            }
        })

        this.text = text
        init()
    }
}

fun Router.buttonView(text: String, init: ButtonView.() -> Unit = {}) = view { ButtonView(text, init) }