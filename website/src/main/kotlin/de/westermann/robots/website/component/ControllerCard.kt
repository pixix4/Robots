package de.westermann.robots.website.component

import de.westermann.robots.datamodel.Controller
import de.westermann.robots.website.toolkit.Router
import de.westermann.robots.website.toolkit.icon.MaterialIcon
import de.westermann.robots.website.toolkit.view.View
import de.westermann.robots.website.toolkit.view.ViewContainer
import de.westermann.robots.website.toolkit.view.ViewList
import de.westermann.robots.website.toolkit.widget.Action
import de.westermann.robots.website.toolkit.widget.IconView
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

    private val name: TextView by ViewContainer(this, "name") { TextView("", "Unnamed") }
    private val code: TextView by ViewContainer(this, "code") { TextView("") }

    private val robots: ViewList<RobotCardMinimal> by ViewContainer(this, "robots") {
        ViewList<RobotCardMinimal>().also { list ->
            list.footer = Action(
                    "Add robot",
                    MaterialIcon.ADD
            ) {
                click.on {
                    it.stopPropagation()

                    addRobotDialog(controller).show()
                }
            }
        }
    }

    init {
        controller.nameProperty.onChangeInit { newValue, _ ->
            name.text = newValue
        }
        controller.adminProperty.onChangeInit { newValue, _ ->
            name.bold = newValue
        }
        controller.codeProperty.onChangeInit { newValue, _ ->
            code.text = newValue
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
                robots += RobotCardMinimal(it, controller)
            }
        }

        click.on {
            Router.routeTo("admin/controllers/${controller.id}")
        }
    }

}