package de.westermann.robots.website.component

import de.westermann.robots.website.WebSocketConnection
import de.westermann.robots.website.toolkit.Router
import de.westermann.robots.website.toolkit.view.View
import de.westermann.robots.website.toolkit.view.ViewContainer
import de.westermann.robots.website.toolkit.widget.Box
import de.westermann.robots.website.toolkit.widget.ButtonView
import de.westermann.robots.website.toolkit.widget.Slider
import de.westermann.robots.website.toolkit.widget.TrackPad
import org.w3c.dom.events.Event
import kotlin.browser.window
import kotlin.math.PI

/**
 * @author lars
 */

class DefaultController : View() {

    private var speed = 0.5

    private val trackPad = TrackPad {
        stepSize = 0.001
        change.on { track ->

            val angle = track.angle
            val radius = track.radius * speed

            when {
                radius == 0.0 -> {
                    WebSocketConnection.iController.drive(0.0, 0.0)
                }
                angle < DEG1 -> {
                    WebSocketConnection.iController.drive(
                            radius,
                            ((angle - DIFF) / DIFF) * radius
                    )
                }
                angle < DEG2 -> {
                    WebSocketConnection.iController.drive(
                            -((angle - DEG1 - DIFF) / DIFF) * radius,
                            radius
                    )
                }
                angle < DEG3 -> {
                    WebSocketConnection.iController.drive(
                            -radius,
                            -((angle - DEG2 - DIFF) / DIFF) * radius
                    )
                }
                else -> {
                    WebSocketConnection.iController.drive(
                            ((angle - DEG3 - DIFF) / DIFF) * radius,
                            -radius
                    )
                }
            }
        }
    }
    private val bottomLeft: Box by ViewContainer(this, "bottom-left") {
        Box {
            +trackPad
        }
    }

    private val slider = Slider {
        stepSize = 0.001
        change.on {
            speed = it
        }
    }
    private val pidButton = ButtonView("Line follower") {
        down.on {
            WebSocketConnection.iController.pid()
        }
    }
    private val topRight: Box by ViewContainer(this, "top-right") { _ ->
        Box {
            +slider
            +pidButton
            +ButtonView("Kick") {
                down.on {
                    WebSocketConnection.iController.kick()
                }
            }
        }
    }

    private fun resize(@Suppress("UNUSED_PARAMETER") event: Event? = null) {
        val landscape = element.clientWidth > element.clientHeight
        slider.vertical = landscape
    }

    init {
        resize()
        window.addEventListener("resize", this::resize)
        window.setTimeout(this::resize, 5)
    }


    companion object {
        val DIFF = PI * 0.25
        val DEG1 = PI * 0.5
        val DEG2 = PI
        val DEG3 = PI * 1.5
    }
}

fun Router.defaultController() = view {
    DefaultController()
}