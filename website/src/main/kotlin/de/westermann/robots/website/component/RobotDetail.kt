package de.westermann.robots.website.component

import de.westermann.robots.datamodel.Robot
import de.westermann.robots.datamodel.util.Button
import de.westermann.robots.datamodel.util.Energy
import de.westermann.robots.datamodel.util.LineFollower
import de.westermann.robots.website.WebSocketConnection
import de.westermann.robots.website.toolkit.icon.MaterialIcon
import de.westermann.robots.website.toolkit.view.View
import de.westermann.robots.website.toolkit.view.ViewContainer
import de.westermann.robots.website.toolkit.view.ViewList
import de.westermann.robots.website.toolkit.widget.*
import kotlin.math.roundToInt

/**
 * @author lars
 */
class RobotDetail(robot: Robot) : View() {

    private val topBox: Box by ViewContainer(this, "top") {
        Box {}
    }
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
                WebSocketConnection.iServer.setName(robot.id, it)
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
            WebSocketConnection.iController.onButton(Button(Button.Type.B, Button.State.DOWN))
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

    private val contentBox: CardView<View> by ViewContainer(this, "content") {
        CardView<View> {
            hoverHighlight = false
            box {
                textView("Controllers")
                +controllers
            }
            +lineFollowerBox
            box {
                textView("Map")
            }
        }
    }

    init {
        robot.nameProperty.onChangeInit { newValue, _ ->
            name.text = newValue
        }

        robot.availableColorsProperty.onChangeInit { list, _ ->
            imageColor.visible = list.isNotEmpty()
        }

        robot.colorProperty.onChangeInit { c, _ ->
            currentColorView.element.style.backgroundColor = c.toString()
        }

        robot.lineFollowerPropety.onChangeInit { l, _ ->
            lineFollowerState.text = when (l.state) {
                LineFollower.State.RUNNING -> "Running"
                LineFollower.State.DISABLED -> "Stopped"
                LineFollower.State.UNAVAILABLE -> "Not available"
            }
            foregroundColorView.element.style.backgroundColor = l.foreground.toString()
            backgroundColorView.element.style.backgroundColor = l.background.toString()

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
    }
}