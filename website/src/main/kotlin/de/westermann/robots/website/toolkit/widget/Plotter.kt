package de.westermann.robots.website.toolkit.widget

import de.westermann.robots.datamodel.util.Coordinate
import de.westermann.robots.website.toolkit.view.TouchEvent
import de.westermann.robots.website.toolkit.view.View
import de.westermann.robots.website.toolkit.view.find
import de.westermann.robots.website.toolkit.view.get
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventListener
import org.w3c.dom.events.MouseEvent
import kotlin.browser.document
import kotlin.browser.window
import kotlin.math.min

/**
 * @author lars
 */
class Plotter : View() {
    private val canvas = (document.createElement("canvas") as HTMLCanvasElement).also {
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
    private val context = (canvas.getContext("2d")
            ?: throw NullPointerException("Drawing is not supported!")) as CanvasRenderingContext2D

    private fun clear() {
        context.clearRect(0.0, 0.0, width.toDouble(), height.toDouble())
    }

    private var userTrack: Boolean = false
    val width
        get() = canvas.width
    val height
        get() = canvas.height

    private var scale: Double = 1.0
    private var transX: Double = 0.0
    private var transY: Double = 0.0

    private var map: List<Coordinate> = emptyList()

    private fun moveTo(coordinate: Coordinate) {
        context.moveTo((coordinate.x * scale) + transX, transY - (coordinate.y * scale))
    }

    private fun lineTo(coordinate: Coordinate) {
        context.lineTo((coordinate.x * scale) + transX, transY - (coordinate.y * scale))
    }

    private fun resetPlotter() {
        val xs = map.map { it.x }
        val ys = map.map { it.y }

        val maxX = xs.max() ?: 0
        val maxY = ys.max() ?: 0
        val minX = xs.min() ?: 0
        val minY = ys.min() ?: 0

        val centerX = (maxX + minX) / 2
        val centerY = -(maxY + minY) / 2

        scale = min(width.toDouble() / (maxX - minX).toDouble(), height.toDouble() / (maxY - minY).toDouble())
        if (scale.isNaN()) scale = 1.0

        scale -= 0.05

        transX = (width / 2.0) - (centerX * scale)
        transY = (height / 2.0) - (centerY * scale)
        userTrack = false
    }

    fun plot(map: List<Coordinate>) {
        val reset = this.map.isEmpty() || (this.map - map).size < this.map.size
        this.map = map

        if (reset) resetPlotter()

        draw()
        window.setTimeout(this::resize, 5)
    }

    fun resetZoom(force: Boolean) {
        if (force || !userTrack) {
            resetPlotter()
        }
        draw()
    }

    private fun draw() {
        clear()
        if (map.isEmpty()) return

        context.beginPath()
        moveTo(map.first())
        map.drop(1).forEach {
            lineTo(it)
        }
        context.strokeStyle = "#000000"
        context.stroke()
    }

    private fun resize(@Suppress("UNUSED_PARAMETER") event: Event? = null) {
        val reset = width == 0 || height == 0
        if (width != element.clientWidth)
            canvas.width = element.clientWidth
        if (height != element.clientHeight)
            canvas.height = element.clientHeight

        if (reset) resetPlotter()

        draw()
    }

    private fun updateTrack(x: Int, y: Int) {
        transX += x - startX
        transY += y - startY

        startX = x
        startY = y

        draw()
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
    private var startX = 0
    private var startY = 0

    private fun startTrack(event: Event) {
        event.preventDefault()
        event.stopPropagation()

        if (identifier != null) return

        userTrack = true

        val offsetX = offsetLeft(canvas)
        val offsetY = offsetTop(canvas)

        if (jsTypeOf(js("TouchEvent")) != "undefined") {
            (event as? TouchEvent)?.let {
                val touch = it.changedTouches[0] ?: return
                identifier = touch.identifier
                startX = touch.clientX - offsetX
                startY = touch.clientY - offsetY

                window.addEventListener("touchmove", moveListener)
                window.addEventListener("touchend", stopListener)
                window.addEventListener("touchcancel", stopListener)
            }
        }

        (event as? MouseEvent)?.let {
            identifier = -1
            startX = it.clientX - offsetX
            startY = it.clientY - offsetY
            window.addEventListener("mousemove", moveListener)
            window.addEventListener("mouseup", stopListener)
        }
    }

    private fun moveTrack(event: Event) {
        event.preventDefault()
        event.stopPropagation()

        val offsetX = offsetLeft(canvas)
        val offsetY = offsetTop(canvas)

        if (jsTypeOf(js("TouchEvent")) != "undefined") {
            (event as? TouchEvent)?.let {
                val touch = it.changedTouches.find(identifier ?: -1) ?: return
                updateTrack(touch.clientX - offsetX, touch.clientY - offsetY)
            }
        }

        (event as? MouseEvent)?.let {
            updateTrack(event.clientX - offsetX, event.clientY - offsetY)
        }
    }

    private fun stopTrack(event: Event) {
        event.preventDefault()
        event.stopPropagation()

        val offsetX = offsetLeft(canvas)
        val offsetY = offsetTop(canvas)

        if (jsTypeOf(js("TouchEvent")) != "undefined") {
            (event as? TouchEvent)?.let {
                val touch = it.changedTouches.find(identifier ?: -1) ?: return
                updateTrack(touch.clientX - offsetX, touch.clientY - offsetY)

                window.removeEventListener("touchmove", moveListener)
                window.removeEventListener("touchend", stopListener)
                window.removeEventListener("touchcancel", stopListener)
                identifier = null
            }
        }

        (event as? MouseEvent)?.let {
            window.removeEventListener("mousemove", moveListener)
            window.removeEventListener("mouseup", stopListener)

            updateTrack(event.clientX - offsetX, event.clientY - offsetY)
            identifier = null
        }
    }


    init {
        resize()
        window.addEventListener("resize", this::resize)
        window.setTimeout(this::resize, 5)
    }
}