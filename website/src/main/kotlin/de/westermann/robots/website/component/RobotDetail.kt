package de.westermann.robots.website.component

import de.westermann.robots.datamodel.DeviceManager
import de.westermann.robots.datamodel.Robot
import de.westermann.robots.datamodel.observe.Library
import de.westermann.robots.datamodel.util.Color
import de.westermann.robots.datamodel.util.Energy
import de.westermann.robots.datamodel.util.LineFollower
import de.westermann.robots.website.WebSocketConnection
import de.westermann.robots.website.toolkit.Router
import de.westermann.robots.website.toolkit.icon.MaterialIcon
import de.westermann.robots.website.toolkit.view.View
import de.westermann.robots.website.toolkit.view.ViewContainer
import de.westermann.robots.website.toolkit.view.ViewList
import de.westermann.robots.website.toolkit.widget.*
import org.w3c.dom.events.MouseEvent
import kotlin.math.roundToInt

/**
 * @author lars
 */
class RobotDetail(robot: Robot) : View() {

    private val topBox: Box by ViewContainer(this, "top") {
        Box {}
    }

    @Suppress("UNUSED")
    private val image: ImageView by ViewContainer(this, "image", topBox.element) {
        robot.colorProperty.onChangeInit { newValue, _ ->
            it.style.backgroundColor = newValue.lightness(0.8).toString()
        }
        ImageView("/public/images/robot.png")
    }

    private val imageColor: IconView by ViewContainer(this, "image-color", topBox.element) {
        IconView(MaterialIcon.PALETTE) {
            click.on {
                val newIndex = (robot.availableColors.indexOf(robot.color) + 1) % robot.availableColors.size
                WebSocketConnection.iServer.setColor(robot.id, robot.availableColors[newIndex])
            }
        }
    }

    private val name: TextView by ViewContainer(this, "name", topBox.element) {
        TextView(robot.name, "Unnamed") {
            editable = true
            edit.on {
                WebSocketConnection.iServer.setRobotName(robot.id, it)
            }
        }
    }

    private val power: Action by ViewContainer(this, "power", topBox.element) {
        Action()
    }


    private val controllers = ViewList<ControllerListItem>().also {
        it.element.classList.add("robot-detail-controllers")
        it.footer = Action(
                "Add controller",
                MaterialIcon.ADD
        ) {
            click.on {
                addControllerDialog(robot).show()
            }
        }
    }

    private val lineFollowerState = TextView {
        click.on {
            when (robot.lineFollower.state) {
                LineFollower.State.RUNNING -> WebSocketConnection.iServer.setPid(robot.id, false)
                LineFollower.State.DISABLED -> WebSocketConnection.iServer.setPid(robot.id, true)
                LineFollower.State.UNAVAILABLE -> {
                }
            }
        }
    }
    private val currentColorView = View()
    private val foregroundColorView = View()
    private val backgroundColorView = View()
    private val lineFollowerBox = Box {
        textView("Line follower")
        box {
            classes += "robot-detail-line-follower"
            box {
                textView("State")
                +lineFollowerState
            }
            box {
                textView("Current color")
                +currentColorView

                click.on {
                    (it as? MouseEvent)?.let {
                        ContextMenu(it.clientX to it.clientY) {
                            item("Set as white") {
                                WebSocketConnection.iServer.setWhitePoint(robot.id, robot.visibleColor)
                            }
                            item("Set as black") {
                                WebSocketConnection.iServer.setBlackPoint(robot.id, robot.visibleColor)
                            }
                            item("Reset calibration") {
                                WebSocketConnection.iServer.setWhitePoint(robot.id, Color.WHITE)
                                WebSocketConnection.iServer.setBlackPoint(robot.id, Color.BLACK)
                            }
                        }.open()
                    }
                }
            }
            box {
                textView("Foreground color")
                +foregroundColorView
                iconView(MaterialIcon.COLORIZE)

                click.on {
                    WebSocketConnection.iServer.setForeground(robot.id)
                }
            }
            box {
                textView("Background color")
                +backgroundColorView
                iconView(MaterialIcon.COLORIZE)

                click.on {
                    WebSocketConnection.iServer.setBackground(robot.id)
                }
            }
        }
    }
    private val plotter = Plotter()
    private val mapBox = Box {
        textView("Map")
        +plotter
    }

    @Suppress("UNUSED")
    private val contentBox: CardView<View> by ViewContainer(this, "content") {
        CardView<View> {
            hoverHighlight = false
            box {
                textView("Controllers")
                +controllers
            }
            +lineFollowerBox
            +mapBox
        }
    }


    private val removeListener = object : Library.Observer<Robot> {
        override fun onRemove(element: Robot) {
            if (element == robot) {
                remove()
            }
        }
    }

    private fun remove() {
        Router.routeUp()
        DeviceManager.robots.onChange(removeListener)
    }

    init {
        robot.nameProperty.onChangeInit { newValue, _ ->
            name.text = newValue
        }

        robot.availableColorsProperty.onChangeInit { list, _ ->
            imageColor.visible = list.isNotEmpty()
        }

        robot.visibleColorProperty.onChangeInit { c, _ ->
            currentColorView.element.style.backgroundColor = c.transform(robot.whitePoint, robot.blackPoint).toString()
        }

        robot.mapProperty.onChangeInit { m, _ ->
            mapBox.visible = m.isNotEmpty()
            plotter.plot(m)
            plotter.resetZoom()
        }

        robot.lineFollowerPropety.onChangeInit { l, _ ->
            lineFollowerState.text = when (l.state) {
                LineFollower.State.RUNNING -> "Running"
                LineFollower.State.DISABLED -> "Stopped"
                LineFollower.State.UNAVAILABLE -> "Not available"
            }
            foregroundColorView.element.style.backgroundColor = l.foreground.transform(robot.whitePoint, robot.blackPoint).toString()
            backgroundColorView.element.style.backgroundColor = l.background.transform(robot.whitePoint, robot.blackPoint).toString()

            lineFollowerBox.visible = l.state != LineFollower.State.UNAVAILABLE
        }

        robot.energyProperty.onChangeInit { energy, _ ->
            power.iconView.icon = when (energy.state) {
                Energy.State.CHARGING -> MaterialIcon.BATTERY_CHARGING_FULL
                Energy.State.DISCHARGING -> MaterialIcon.BATTERY_STD
                Energy.State.UNKNOWN -> MaterialIcon.BATTERY_UNKNOWN
                Energy.State.NO_BATTERY -> MaterialIcon.POWER
            }

            power.text.text = if (energy.state == Energy.State.NO_BATTERY) "" else {
                "${(energy.value * 100).roundToInt()}%"
            }
        }

        robot.controllersProperty.onChangeInit { newValue, _ ->
            controllers.clear()
            newValue.forEach {
                controllers += ControllerListItem(it, robot)
            }
        }

        val colorPointChange = { _: Color, _: Color ->
            currentColorView.element.style.backgroundColor = robot.visibleColor.transform(robot.whitePoint, robot.blackPoint).toString()
            foregroundColorView.element.style.backgroundColor = robot.lineFollower.foreground.transform(robot.whitePoint, robot.blackPoint).toString()
            backgroundColorView.element.style.backgroundColor = robot.lineFollower.background.transform(robot.whitePoint, robot.blackPoint).toString()
        }

        robot.whitePointPropety.onChange(colorPointChange)
        robot.blackPointPropety.onChange(colorPointChange)

        DeviceManager.robots.onChange(removeListener)
    }
}