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

    val panel = (document.createElement("div") as HTMLElement).also {
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
    val track = (document.createElement("div") as HTMLElement).also {
        panel.appendChild(it)
    }

    var sticky: Boolean = false

    val change = EventHandler<Track>()

    fun resize(event: Event? = null) {
        val size = min(element.clientWidth, element.clientHeight)
        panel.style.width = "${size}px"
        panel.style.height = "${size}px"
    }

    fun offsetLeft(element: HTMLElement): Double = element.offsetLeft + ((element.parentElement as? HTMLElement)?.let {
        offsetLeft(it)
    } ?: 0.0)

    fun offsetTop(element: HTMLElement): Double = element.offsetTop + ((element.parentElement as? HTMLElement)?.let {
        offsetTop(it)
    } ?: 0.0)

    private var currentPosition: Track = Track(0.0, 0.0)
    private fun updateTrack(x: Double, y: Double) =
            updateTrack(Track((x - 0.5) * 2, (0.5 - y) * 2).normalize())

    private fun updateTrack(t: Track) {
        track.style.left = "${(t.x / 2 + 0.5) * 100}%"
        track.style.top = "${(-t.y / 2 + 0.5) * 100}%"

        currentPosition = t
        change.fire(t)
    }


    val moveListener = object : EventListener {
        override fun handleEvent(event: Event) {
            moveTrack(event)
        }
    }

    val stopListener = object : EventListener {
        override fun handleEvent(event: Event) {
            stopTrack(event)
        }
    }

    private var identifier: Int? = null
    private var interval: Int? = null

    fun startTrack(event: Event) {
        if (identifier != null) return

        interval?.let {
            window.clearInterval(it)
        }

        val offsetX = offsetLeft(panel)
        val offsetY = offsetTop(panel)

        (event as? MouseEvent)?.let {
            identifier = -1
            updateTrack(
                    (event.clientX - offsetX) / panel.clientWidth.toDouble(),
                    (event.clientY - offsetY) / panel.clientHeight.toDouble()
            )
            window.addEventListener("mousemove", moveListener)
            window.addEventListener("mouseup", stopListener)
        }

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
    }

    fun moveTrack(event: Event) {
        val offsetX = offsetLeft(panel)
        val offsetY = offsetTop(panel)
        (event as? MouseEvent)?.let {
            updateTrack(
                    (event.clientX - offsetX) / panel.clientWidth.toDouble(),
                    (event.clientY - offsetY) / panel.clientHeight.toDouble()
            )
        }

        if (jsTypeOf(js("TouchEvent")) != "undefined") {
            (event as? TouchEvent)?.let {
                val touch = it.changedTouches.find(identifier ?: -1) ?: return
                updateTrack(
                        (touch.clientX - offsetX) / panel.clientWidth.toDouble(),
                        (touch.clientY - offsetY) / panel.clientHeight.toDouble()
                )
            }
        }
    }

    fun stopTrack(event: Event) {
        val offsetX = offsetLeft(panel)
        val offsetY = offsetTop(panel)
        (event as? MouseEvent)?.let {
            window.removeEventListener("mousemove", moveListener)
            window.removeEventListener("mouseup", stopListener)

            updateTrack(
                    (event.clientX - offsetX) / panel.clientWidth.toDouble(),
                    (event.clientY - offsetY) / panel.clientHeight.toDouble()
            )
        }

        if (jsTypeOf(js("TouchEvent")) != "undefined") {
            (event as? TouchEvent)?.let {
                window.removeEventListener("touchmove", moveListener)
                window.removeEventListener("touchend", stopListener)

                val touch = it.changedTouches.find(identifier ?: -1) ?: return
                updateTrack(
                        (touch.clientX - offsetX) / panel.clientWidth.toDouble(),
                        (touch.clientY - offsetY) / panel.clientHeight.toDouble()
                )
            }
        }
        identifier = null

        if (!sticky) {
            interval = window.setInterval({
                val oldRadius = currentPosition.radius
                val newRadius = oldRadius - max(oldRadius / 8.0, 0.01)
                println("old: $oldRadius | new: $newRadius")
                updateTrack(currentPosition.radius(newRadius))
                if (currentPosition.isZero) {
                    interval?.let { window.clearInterval(it) }
                }
            }, 20)
        }
    }

    init {
        init()
        resize()
        window.addEventListener("resize", this::resize)

        change.on {
            println(it)
        }
    }
}

fun Router.trackPad(init: TrackPad.() -> Unit = {}) = view { TrackPad(init) }