package de.westermann.robots.website.toolkit.widget

import de.westermann.robots.website.toolkit.icon.Icon
import de.westermann.robots.website.toolkit.view.SelectableViewList
import de.westermann.robots.website.toolkit.view.View
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventListener
import org.w3c.dom.events.KeyboardEvent
import kotlin.browser.document
import kotlin.browser.window
import kotlin.math.min

/**
 * @author lars
 */
class ContextMenu(
        private val position: Pair<Int, Int>,
        init: ContextMenu.() -> Unit
) : View() {

    private val list = SelectableViewList<Action>().also {
        element.appendChild(it.element)
    }

    private val closeListener = object : EventListener {
        override fun handleEvent(event: Event) {
            (event as? KeyboardEvent)?.let {
                when (it.keyCode) {
                    27 -> close()
                    37, 38 -> list.selectionUp()
                    39, 40 -> list.selectionUp()
                    13, 32 -> list.selectionExec()
                    else -> return@let
                }
                it.preventDefault()
                it.stopPropagation()
            }
        }
    }

    fun item(text: String = "", icon: Icon? = null, onClick: (Event) -> Unit = {}) {
        list.add(Action(text, icon) {
            click.on(onClick)
        })
    }

    fun open() {
        window.addEventListener("keyup", closeListener)
        document.body?.appendChild(element)

        resize()
        window.setTimeout(this::resize, 5)
    }

    fun close() {
        window.removeEventListener("keyup", closeListener)
        element.parentElement?.removeChild(element)
    }

    private fun resize(@Suppress("UNUSED_PARAMETER") event: Event? = null) {
        val left = min(position.first, window.innerWidth - list.element.clientWidth - 10)
        val top = min(position.second, window.innerHeight - list.element.clientHeight - 10)

        list.element.style.left = "${left}px"
        list.element.style.top = "${top}px"
    }

    init {
        click.on {
            close()
        }
        init()

        resize()
        window.addEventListener("resize", this::resize)
        window.setTimeout(this::resize, 5)
    }
}