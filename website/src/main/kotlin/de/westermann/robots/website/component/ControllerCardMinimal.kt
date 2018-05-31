package de.westermann.robots.website.component

import de.westermann.robots.datamodel.Controller
import de.westermann.robots.datamodel.Robot
import de.westermann.robots.website.WebSocketConnection
import de.westermann.robots.website.toolkit.Router
import de.westermann.robots.website.toolkit.icon.MaterialIcon
import de.westermann.robots.website.toolkit.view.View
import de.westermann.robots.website.toolkit.view.ViewContainer
import de.westermann.robots.website.toolkit.widget.ContextMenu
import de.westermann.robots.website.toolkit.widget.IconView
import de.westermann.robots.website.toolkit.widget.TextView
import org.w3c.dom.events.MouseEvent

/**
 * @author lars
 */
class ControllerCardMinimal(controller: Controller, robot: Robot) : View() {

    private val icon: IconView by ViewContainer(this, "icon") {
        controller.colorProperty.onChangeInit { newValue, _ ->
            it.style.color = newValue.lightness(0.6).toString()
        }
        IconView()
    }

    private val name: TextView by ViewContainer(this, "name") { TextView(controller.name, "Unnamed") }
    private val code: TextView by ViewContainer(this, "code") { TextView(controller.code) }

    init {
        controller.nameProperty.onChange { newValue, _ ->
            name.text = newValue
        }
        controller.adminProperty.onChangeInit { newValue, _ ->
            name.bold = newValue
        }
        controller.codeProperty.onChange { newValue, _ ->
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

        click.on {
            it.stopPropagation()

            ContextMenu(((it as? MouseEvent)?.clientX ?: 0) to ((it as? MouseEvent)?.clientY ?: 0)) {
                item("Open controller details") {
                    Router.routeTo("admin/controllers/${controller.id}")
                }
                item("Remove controller") {
                    WebSocketConnection.iServer.unbind(controller.id, robot.id)
                }
                open()
            }
        }
    }
}