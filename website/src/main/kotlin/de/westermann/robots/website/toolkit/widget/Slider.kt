package de.westermann.robots.website.toolkit.widget

import de.westermann.robots.website.toolkit.Router
import de.westermann.robots.website.toolkit.view.*
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventListener
import org.w3c.dom.events.MouseEvent
import kotlin.browser.document
import kotlin.browser.window
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * @author lars
 */
class Slider(
        init: Slider.() -> Unit
) : View() {

    private val panel = (document.createElement("div") as HTMLElement).also {
        element.appendChild(it)
    }
    private val slide = (document.createElement("div") as HTMLElement).also {
        panel.appendChild(it)
    }

    var vertical: Boolean = false
        set(value) {
            field = value
            element.classList.toggle("vertical", value)
            updateSlider(currentPosition)
        }

    var disable: Boolean
        get() = element.classList.contains("disabled")
        set(value) {
            element.classList.toggle("disabled", value)
        }

    val change = EventHandler<Double>(250)

    var stepSize: Double = 0.0
        set(value) {
            field = value
            updateSlider(currentPosition)
        }

    private var currentPosition: Double = 0.5

    private fun updateSlider(position: Double) {
        val oldPosition = currentPosition
        currentPosition = min(1.0, max(0.0, position))
        if (stepSize > 0.0) {
            currentPosition = (currentPosition / stepSize).roundToInt() * stepSize
        }

        if (vertical) {
            slide.style.left = "0"
            slide.style.top = "${(1 - currentPosition) * 100}%"
        } else {
            slide.style.top = "0"
            slide.style.left = "${currentPosition * 100}%"
        }

        if (currentPosition != oldPosition)
            change.fire(currentPosition)
    }

    private fun internalUpdate(clientPos: Int) {
        val offset = if (vertical) offsetTop(panel) else offsetLeft(panel)
        val size = if (vertical) panel.clientHeight.toDouble() else panel.clientWidth.toDouble()
        val raw = (clientPos - offset) / size
        updateSlider(if (vertical) 1 - raw else raw)
    }

    private fun updateSlider(event: MouseEvent) =
            internalUpdate(if (vertical) event.clientY else event.clientX)


    private fun updateSlider(touch: Touch) =
            internalUpdate(if (vertical) touch.clientY else touch.clientX)


    private val moveListener = object : EventListener {
        override fun handleEvent(event: Event) {
            moveSlider(event)
        }
    }

    private val stopListener = object : EventListener {
        override fun handleEvent(event: Event) {
            stopSlider(event)
        }
    }

    private var identifier: Int? = null

    private fun startSlider(event: Event) {
        event.preventDefault()
        event.stopPropagation()

        if (identifier != null || disable) return

        if (jsTypeOf(js("TouchEvent")) != "undefined") {
            (event as? TouchEvent)?.let {
                val touch = it.changedTouches[0] ?: return
                identifier = touch.identifier
                updateSlider(touch)

                window.addEventListener("touchmove", moveListener)
                window.addEventListener("touchend", stopListener)
                window.addEventListener("touchcancel", stopListener)
            }
        }

        (event as? MouseEvent)?.let {
            identifier = -1
            updateSlider(it)
            window.addEventListener("mousemove", moveListener)
            window.addEventListener("mouseup", stopListener)
        }
    }

    private fun moveSlider(event: Event) {
        event.preventDefault()
        event.stopPropagation()

        if (jsTypeOf(js("TouchEvent")) != "undefined") {
            (event as? TouchEvent)?.let {
                updateSlider(it.changedTouches.find(identifier ?: -1) ?: return)
            }
        }

        (event as? MouseEvent)?.let {
            updateSlider(it)
        }
    }

    private fun stopSlider(event: Event) {
        event.preventDefault()
        event.stopPropagation()

        if (jsTypeOf(js("TouchEvent")) != "undefined") {
            (event as? TouchEvent)?.let {
                updateSlider(it.changedTouches.find(identifier ?: -1) ?: return)

                window.removeEventListener("touchmove", moveListener)
                window.removeEventListener("touchend", stopListener)
                window.removeEventListener("touchcancel", stopListener)
                identifier = null
            }
            return
        }

        (event as? MouseEvent)?.let {
            window.removeEventListener("mousemove", moveListener)
            window.removeEventListener("mouseup", stopListener)

            updateSlider(it)
            identifier = null
        }

    }

    init {
        element.addEventListener("mousedown", this::startSlider)
        element.addEventListener("touchstart", this::startSlider)
        element.addEventListener("contextmenu", object : EventListener {
            override fun handleEvent(event: Event) {
                event.stopPropagation()
                event.preventDefault()
            }
        })

        init()
    }
}

fun Router.slider(init: Slider.() -> Unit = {}) = view { Slider(init) }