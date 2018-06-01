package de.westermann.robots.website.toolkit.widget

import de.westermann.robots.datamodel.util.Track
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

/**
 * @author lars
 */
class TrackPad(
        init: TrackPad.() -> Unit
) : View() {

    private val panel = (document.createElement("div") as HTMLElement).also {
        element.appendChild(it)

        it.addEventListener("mousedown", this::startTrack)
        it.addEventListener("touchstart", this::startTrack)
        it.addEventListener("contextmenu", object : EventListener {
            override fun handleEvent(event: Event) {
                event.stopPropagation()
                event.preventDefault()
            }
        })
    }
    private val track = (document.createElement("div") as HTMLElement).also {
        panel.appendChild(it)
    }

    var sticky: Boolean = false
    var disable: Boolean
        get() = element.classList.contains("disabled")
        set(value) {
            element.classList.toggle("disabled", value)
        }

    val change = TimeoutEventHandler<Track>(250)

    private fun resize(@Suppress("UNUSED_PARAMETER") event: Event? = null) {
        val size = min(element.clientWidth, element.clientHeight)
        panel.style.width = "${size}px"
        panel.style.height = "${size}px"

        panel.style.marginLeft = "-${size / 2}px"
        panel.style.marginTop = "-${size / 2}px"
    }

    var stepSize: Double = 0.0
        set(value) {
            field = value
            updateTrack(currentPosition)
        }

    private var currentPosition: Track = Track(0.0, 0.0)
    private fun updateTrack(x: Double, y: Double) =
            updateTrack(Track((x - 0.5) * 2, (0.5 - y) * 2).normalize())

    private fun updateTrack(position: Track) {
        val oldPosition = currentPosition

        currentPosition = if (stepSize > 0.0) {
            position.discretize(stepSize)
        } else {
            position
        }

        track.style.left = "${(currentPosition.x / 2 + 0.5) * 100}%"
        track.style.top = "${(-currentPosition.y / 2 + 0.5) * 100}%"

        if (oldPosition != currentPosition)
            change.fire(currentPosition)
    }


    private val moveListener = object : EventListener {
        override fun handleEvent(event: Event) {
            moveTrack(event)
        }
    }

    private val stopListener = object : EventListener {
        override fun handleEvent(event: Event) {
            stopTrack(event)
        }
    }

    private var identifier: Int? = null
    private var interval: Int? = null

    private fun startTrack(event: Event) {
        event.preventDefault()
        event.stopPropagation()

        if (identifier != null || disable) return

        interval?.let {
            window.clearInterval(it)
        }

        val offsetX = offsetLeft(panel)
        val offsetY = offsetTop(panel)

        if (jsTypeOf(js("TouchEvent")) != "undefined") {
            (event as? TouchEvent)?.let {
                val touch = it.changedTouches[0] ?: return
                identifier = touch.identifier
                updateTrack(
                        (touch.clientX - offsetX) / panel.clientWidth.toDouble(),
                        (touch.clientY - offsetY) / panel.clientHeight.toDouble()
                )

                window.addEventListener("touchmove", moveListener)
                window.addEventListener("touchend", stopListener)
                window.addEventListener("touchcancel", stopListener)
            }
        }

        (event as? MouseEvent)?.let {
            identifier = -1
            updateTrack(
                    (event.clientX - offsetX) / panel.clientWidth.toDouble(),
                    (event.clientY - offsetY) / panel.clientHeight.toDouble()
            )
            window.addEventListener("mousemove", moveListener)
            window.addEventListener("mouseup", stopListener)
        }
    }

    private fun moveTrack(event: Event) {
        event.preventDefault()
        event.stopPropagation()

        val offsetX = offsetLeft(panel)
        val offsetY = offsetTop(panel)

        if (jsTypeOf(js("TouchEvent")) != "undefined") {
            (event as? TouchEvent)?.let {
                val touch = it.changedTouches.find(identifier ?: -1) ?: return
                updateTrack(
                        (touch.clientX - offsetX) / panel.clientWidth.toDouble(),
                        (touch.clientY - offsetY) / panel.clientHeight.toDouble()
                )
            }
        }

        (event as? MouseEvent)?.let {
            updateTrack(
                    (event.clientX - offsetX) / panel.clientWidth.toDouble(),
                    (event.clientY - offsetY) / panel.clientHeight.toDouble()
            )
        }
    }

    private fun stopTrack(event: Event) {
        event.preventDefault()
        event.stopPropagation()

        val offsetX = offsetLeft(panel)
        val offsetY = offsetTop(panel)

        if (jsTypeOf(js("TouchEvent")) != "undefined") {
            (event as? TouchEvent)?.let {
                val touch = it.changedTouches.find(identifier ?: -1) ?: return
                updateTrack(
                        (touch.clientX - offsetX) / panel.clientWidth.toDouble(),
                        (touch.clientY - offsetY) / panel.clientHeight.toDouble()
                )

                window.removeEventListener("touchmove", moveListener)
                window.removeEventListener("touchend", stopListener)
                window.removeEventListener("touchcancel", stopListener)
                identifier = null
            }
        }

        (event as? MouseEvent)?.let {
            window.removeEventListener("mousemove", moveListener)
            window.removeEventListener("mouseup", stopListener)

            updateTrack(
                    (event.clientX - offsetX) / panel.clientWidth.toDouble(),
                    (event.clientY - offsetY) / panel.clientHeight.toDouble()
            )
            identifier = null
        }


        if (!sticky && identifier == null) {
            interval = window.setInterval({
                val oldRadius = currentPosition.radius
                val newRadius = oldRadius - max(oldRadius / 10.0, 0.01)
                updateTrack(currentPosition.radius(newRadius))
                if (currentPosition.isZero) {
                    interval?.let { window.clearInterval(it) }
                }
            }, 15)
        }
    }

    init {
        init()
        resize()
        window.addEventListener("resize", this::resize)
        window.setTimeout(this::resize, 5)
    }
}

fun Router.trackPad(init: TrackPad.() -> Unit = {}) = view { TrackPad(init) }