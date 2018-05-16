package de.westermann.robots.website.toolkit.widget

import de.westermann.robots.website.toolkit.Router
import de.westermann.robots.website.toolkit.icon.Icon
import de.westermann.robots.website.toolkit.view.EventHandler
import de.westermann.robots.website.toolkit.view.View
import de.westermann.robots.website.toolkit.view.ViewContainer
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventListener
import org.w3c.dom.events.KeyboardEvent
import kotlin.browser.document

/**
 * @author lars
 */

class Input(
        value: String = "",
        init: Input.() -> Unit
) : View() {


    private val iconView: IconView by ViewContainer(this, "icon") {
        IconView()
    }

    private val inputElement: HTMLInputElement = (document.createElement("input") as HTMLInputElement).also {
        element.appendChild(it)
        it.addEventListener("keyup", object : EventListener {
            override fun handleEvent(event: Event) {
                (event as? KeyboardEvent)?.let {
                    if (it.keyCode == 13) {
                        submit.fire(value)
                    } else {
                        change.fire(value)
                    }
                }
            }
        })
        it.addEventListener("change", object : EventListener {
            override fun handleEvent(event: Event) {
                change.fire(value)
            }
        })
    }

    var icon: Icon?
        get() = iconView.icon
        set(value) {
            inputElement.classList.toggle("icon", value != null)
            iconView.icon = value
        }

    var value: String
        get() = inputElement.value
        set(value) {
            inputElement.value = value
        }

    var placeholder: String
        get() = inputElement.placeholder
        set(value) {
            inputElement.placeholder = value
        }

    var type: Type
        get() = Type.find(inputElement.type)
    set(value) {
        inputElement.type = value.name.toLowerCase()
    }

    val change = EventHandler<String>()
    val submit = EventHandler<String>()

    fun focus() {
        inputElement.focus()
    }

    init {
        this.value = value
        init()
    }

    enum class Type {
        TEXT, PASSWORD;

        companion object {
            fun find(type: String): Type = Type.values().find {
                    it.name.equals(type, true)
                } ?: TEXT

        }
    }
}


fun Router.input(value: String, init: Input.() -> Unit = {}) = view { Input(value, init) }
