package de.westermann.robots.website.toolkit.widget

import de.westermann.robots.website.toolkit.icon.MaterialIcon
import de.westermann.robots.website.toolkit.view.View
import de.westermann.robots.website.toolkit.view.ViewContainer
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventListener
import org.w3c.dom.events.KeyboardEvent
import kotlin.browser.document
import kotlin.browser.window
import kotlin.math.max
import kotlin.math.min

/**
 * @author lars
 */
class Dialog(
        init: Dialog.() -> Unit = {}
) : View() {
    private val titleTextView = TextView()

    private val topBox = Box {
        +titleTextView

        +IconView(MaterialIcon.CLOSE) {
            click.on {
                hide()
            }
        }
        element.classList.add("dialog-top")
    }

    var title: String
        get() = titleTextView.text
        set(value) {
            titleTextView.text = value
        }

    var titleBar: Boolean
        get() = topBox.visible
        set(value) {
            topBox.visible = value
        }

    private val dialog = (document.createElement("div") as HTMLElement).also {
        it.classList.add("dialog-window")
        it.appendChild(topBox.element)

        it.addEventListener("click", object : EventListener {
            override fun handleEvent(event: Event) {
                event.stopPropagation()
            }
        })
        element.appendChild(it)
    }

    var content: View? by ViewContainer(this, "content", dialog) {
        null
    }

    private val closeListener = object : EventListener {
        override fun handleEvent(event: Event) {
            (event as? KeyboardEvent)?.let {
                when (it.keyCode) {
                    27 -> hide()
                    else -> return@let
                }
                it.preventDefault()
                it.stopPropagation()
            }
        }
    }

    fun show() {
        window.addEventListener("keyup", closeListener)
        document.body?.appendChild(element)

        window.addEventListener("resize", resizeListener)
        window.setTimeout(this::resize, 1)
    }

    fun hide() {
        window.removeEventListener("keyup", closeListener)
        element.parentElement?.removeChild(element)
        window.removeEventListener("resize", resizeListener)
    }

    private val resizeListener = object : EventListener {
        override fun handleEvent(event: Event) {
            resize()
        }
    }

    fun resize() {
        val maxHeight = window.innerHeight - 32
        val reqHeight = (content?.element?.clientHeight ?: 0) + topBox.element.clientHeight
        val height = max(maxHeight / 2, min(reqHeight, maxHeight))

        dialog.style.height = "${height}px"
        dialog.style.marginTop = "-${height / 2}px"
    }

    init {
        click.on {
            hide()
        }
        init()
    }
}