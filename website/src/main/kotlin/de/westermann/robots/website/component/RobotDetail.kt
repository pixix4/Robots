package de.westermann.robots.website.component

import de.westermann.robots.datamodel.Robot
import de.westermann.robots.website.toolkit.icon.MaterialIcon
import de.westermann.robots.website.toolkit.view.View
import de.westermann.robots.website.toolkit.view.ViewContainer
import de.westermann.robots.website.toolkit.view.ViewList
import de.westermann.robots.website.toolkit.widget.*

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

    private val name: TextView by ViewContainer(this, "name", topBox.element) { TextView(robot.name, "Unnamed") }


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
    private val contentBox: CardView<View> by ViewContainer(this, "content") {
        CardView<View> {
            box {
                textView("Controllers")
                +controllers
            }
            box {
                textView("Drive")
            }
            box {
                textView("Line follower")
            }
            box {
                textView("Map")
            }
        }
    }

    init {
        robot.nameProperty.onChange { newValue, _ ->
            name.text = newValue
        }

        robot.controllersProperty.onChangeInit { newValue, _ ->
            controllers.clear()
            newValue.forEach {
                controllers += ControllerListItem(it, robot)
            }
        }
    }
}