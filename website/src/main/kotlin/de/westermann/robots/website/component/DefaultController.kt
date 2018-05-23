package de.westermann.robots.website.component

import de.westermann.robots.datamodel.util.Button
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

/**
 * @author lars
 */

class DefaultController : View() {
    private val trackPad = TrackPad {
        stepSize = 0.001
        change.on {
            WebSocketConnection.iController.onTrack(it)
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
            WebSocketConnection.iController.onAbsoluteSpeed(it)
        }
    }
    private val topRight: Box by ViewContainer(this, "top-right") {
        Box {
            +slider
            +ButtonView("Kick") {
                down.on {
                    WebSocketConnection.iController.onButton(Button(Button.Type.A, Button.State.DOWN))
                }
                up.on {
                    WebSocketConnection.iController.onButton(Button(Button.Type.A, Button.State.UP))
                }
            }
        }
    }

    private fun resize(event: Event? = null) {
        val landscape = element.clientWidth > element.clientHeight
        slider.vertical = landscape
    }

    init {
        resize()
        window.addEventListener("resize", this::resize)
        window.setTimeout(this::resize, 5)
    }
}

fun Router.defaultController() = view {
    DefaultController()
}