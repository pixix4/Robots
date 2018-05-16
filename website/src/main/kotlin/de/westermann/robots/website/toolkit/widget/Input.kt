package de.westermann.robots.website.toolkit.widget

import de.westermann.robots.website.toolkit.Router
import de.westermann.robots.website.toolkit.icon.Icon
import de.westermann.robots.website.toolkit.view.EventHandler
import de.westermann.robots.website.toolkit.view.View
import de.westermann.robots.website.toolkit.view.ViewContainer
import org.w3c.dom.HTMLInputElement
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

    val change = EventHandler<String>()

    fun focus() {
        inputElement.focus()
    }

    init {
        this.value = value
        init()
    }
}


fun Router.input(value: String, init: Input.() -> Unit = {}) = view { Input(value, init) }
