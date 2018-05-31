package de.westermann.robots.website.toolkit.widget

import de.westermann.robots.website.toolkit.view.TimeoutEventHandler
import de.westermann.robots.website.toolkit.view.View
import de.westermann.robots.website.toolkit.view.ViewList
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventListener
import org.w3c.dom.events.KeyboardEvent
import kotlin.browser.window

/**
 * @author lars
 */

class TextView(text: String = "", placeholder: String = "", init: TextView.() -> Unit = {}) : View() {

    var text: String = text
        set(value) {
            field = value
            update()
        }

    var placeholder: String = placeholder
        set(value) {
            field = value
            update()
        }

    private fun update() {
        if (text.isEmpty()) {
            element.textContent = placeholder
            element.classList.add("placeholder")
        } else {
            element.textContent = text
            element.classList.remove("placeholder")
        }
    }

    var editable: Boolean
        get() = element.classList.contains("editable")
        set(value) {
            element.classList.toggle("editable", value)
        }

    var bold: Boolean
        get() = element.classList.contains("bold")
        set(value) {
            element.classList.toggle("bold", value)
        }

    private val editStopListener = EventListener {
        editing = false
    }

    var editing: Boolean
        get() = element.getAttribute("contenteditable") == "true"
        set(value) {
            val submit = !value && editing
            element.setAttribute("contenteditable", value.toString())

            if (value) {
                window.addEventListener("click", editStopListener)
            } else {
                window.removeEventListener("click", editStopListener)
                element.blur()
            }

            if (submit && text != element.textContent) {
                text = element.textContent ?: ""
                edit.fire(text)
            }
        }

    val edit = TimeoutEventHandler<String>()

    init {
        this.text = text

        click.on {
            if (editable) {
                it.stopPropagation()

                if (!editing) {
                    editing = true
                    element.focus()
                }
            }
        }

        element.addEventListener("keydown", object : EventListener {
            override fun handleEvent(event: Event) {
                if (editing) {
                    (event as? KeyboardEvent)?.let {
                        when (it.keyCode) {
                            13 -> {
                                it.preventDefault()
                                it.stopPropagation()
                                editing = false
                            }
                            27 -> {
                                it.preventDefault()
                                it.stopPropagation()
                                update()
                                editing = false
                            }
                        }
                    }
                }
            }
        })

        init()
    }
}

fun ViewList<View>.textView(text: String = "", placeholder: String = "") {
    this += TextView(text, placeholder)
}