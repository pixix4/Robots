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
class ControllerCardMinimal(controller: Controller) : View() {

    private val icon: IconView by ViewContainer(this, "icon") {
        controller.colorProperty.onChangeInit { newValue, _ ->
            it.style.color = newValue.lightness(0.6).toString()
        }
        IconView()
    }

    private val name: TextView by ViewContainer(this, "name") { TextView(controller.name, "Unnamed") }

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

        click.on {
            it.stopPropagation()
            println("Open controller context menu: $controller")
        }
    }


}