package de.westermann.robots.website.component

import de.westermann.robots.datamodel.Robot
import de.westermann.robots.website.toolkit.Router
import de.westermann.robots.website.toolkit.icon.MaterialIcon
import de.westermann.robots.website.toolkit.view.View
import de.westermann.robots.website.toolkit.view.ViewContainer
import de.westermann.robots.website.toolkit.view.ViewList
import de.westermann.robots.website.toolkit.widget.IconView
import de.westermann.robots.website.toolkit.widget.ImageView
import de.westermann.robots.website.toolkit.widget.TextView
import kotlin.browser.window

/**
 * @author lars
 */
class RobotCard(robot: Robot) : View() {

    private val image: ImageView by ViewContainer(this, "image") {
        robot.colorProperty.onChangeInit { newValue, _ ->
            it.style.backgroundColor = newValue.lightness(0.8).toString()
        }
        ImageView("/public/images/robot.png")
    }

    private val name: TextView by ViewContainer(this, "name") { TextView(robot.name, "Unnamed") }

    private val moduleList: ViewList<IconView> by ViewContainer(this, "modules") {
        ViewList<IconView>()
    }

    private val controllers: ViewList<ControllerCardMinimal> by ViewContainer(this, "controllers") {
        ViewList<ControllerCardMinimal>().also { list ->
            list.click.on {
                if (list.isEmpty()) {
                    it.stopPropagation()
                    println("Add controller to robot: $robot")
                }
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
                controllers += ControllerCardMinimal(it)
            }
        }

        IconView(MaterialIcon.VIDEOCAM).also {
            moduleList += it
            robot.cameraProperty.onChangeInit { newValue, _ ->
                it.visible = !newValue.available
            }
        }

        IconView(MaterialIcon.SETTINGS_ETHERNET).also {
            moduleList += it
            robot.kickerProperty.onChangeInit { newValue, _ ->
                it.visible = !newValue.available
            }
        }

        click.on {
            Router.routeTo("admin/robots/${robot.id}")
        }
    }

}