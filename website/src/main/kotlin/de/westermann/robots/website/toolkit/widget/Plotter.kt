package de.westermann.robots.website.toolkit.widget

import de.westermann.robots.datamodel.util.Coordinate
import de.westermann.robots.website.toolkit.view.View
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.events.Event
import kotlin.browser.document
import kotlin.browser.window
import kotlin.math.min

/**
 * @author lars
 */
class Plotter : View() {
    private val canvas = (document.createElement("canvas") as HTMLCanvasElement).also {
        element.appendChild(it)
    }
    private val context = (canvas.getContext("2d")
            ?: throw NullPointerException("Drawing is not supported!")) as CanvasRenderingContext2D

    private fun clear() {
        context.clearRect(0.0, 0.0, width.toDouble(), height.toDouble())
    }


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

        scale -= 0.1

        transX = (width / 2.0) - (centerX * scale)
        transY = (height / 2.0) - (centerY * scale)
    }

    fun plot(map: List<Coordinate>) {
        val reset = this.map.isEmpty() || (this.map - map).size < this.map.size
        this.map = map

        if (reset) resetPlotter()

        draw()
        window.setTimeout(this::resize, 5)
    }

    fun resetZoom() {
        resetPlotter()
        draw()
    }

    private fun draw() {
        clear()
        if (map.isEmpty()) return

        moveTo(map.first())
        map.drop(1).forEach {
            lineTo(it)
        }
        context.strokeStyle = "#000000"
        context.stroke()
    }

    private fun resize(@Suppress("UNUSED_PARAMETER") event: Event? = null) {
        val reset = width == 0 || height == 0
        canvas.width = element.clientWidth
        canvas.height = element.clientHeight

        if (reset) resetPlotter()

        draw()
    }

    init {
        resize()
        window.addEventListener("resize", this::resize)
        window.setTimeout(this::resize, 5)
    }
}