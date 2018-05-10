package de.westermann.robots.website.component

import de.westermann.robots.datamodel.Controller
import de.westermann.robots.website.toolkit.icon.MaterialIcon
import de.westermann.robots.website.toolkit.view.View
import de.westermann.robots.website.toolkit.view.ViewContainer
import de.westermann.robots.website.toolkit.view.ViewList
import de.westermann.robots.website.toolkit.widget.IconView
import de.westermann.robots.website.toolkit.widget.ImageView
import de.westermann.robots.website.toolkit.widget.TextView

/**
 * @author lars
 */
class ControllerCard(controller: Controller) : View() {

    private val icon: IconView by ViewContainer(this, "icon") {
        controller.colorProperty.onChangeInit { newValue, _ ->
            it.style.backgroundColor = newValue.lightness(0.8).toString()
        }
        IconView()
    }

    private val name: TextView by ViewContainer(this, "name") { TextView(controller.name, "Unnamed") }


    private val robots: ViewList<RobotCardMinimal> by ViewContainer(this, "robots") {
        ViewList<RobotCardMinimal>().also { list ->
            list.click.on {
                if (list.isEmpty()) {
                    it.stopPropagation()
                    println("Add robot to controller: $controller")
                }
            }
        }
    }

    init {
        controller.nameProperty.onChange { newValue, _ ->
            name.text = newValue
        }

        controller.typeProperty.onChangeInit { newValue, _ ->
            icon.icon = when (newValue) {
                Controller.Type.DESKTOP -> MaterialIcon.DESKTOP_WINDOWS
                Controller.Type.MOBIL -> MaterialIcon.SMARTPHONE
                Controller.Type.PHYSICAL -> MaterialIcon.VIDEOGAME_ASSET
                Controller.Type.UNKNOWN -> MaterialIcon.HELP_OUTLINE
            }
        }

        controller.robotsProperty.onChangeInit { newValue, _ ->
            robots.clear()
            newValue.forEach {
                robots += RobotCardMinimal(it)
            }
        }

        click.on {
            println("Open controller details: $controller")
        }
    }

}