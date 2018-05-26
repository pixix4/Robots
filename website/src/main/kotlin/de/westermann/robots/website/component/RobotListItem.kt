package de.westermann.robots.website.component

import de.westermann.robots.datamodel.Controller
import de.westermann.robots.datamodel.Robot
import de.westermann.robots.datamodel.util.Color
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
class RobotListItem(robot: Robot, controller: Controller, onClickListener: () -> Boolean = { false }) : View() {

    private val icon: IconView by ViewContainer(this, "icon") {
        it.style.color = Color.BLACK.lightness(0.6).toString()
        IconView(MaterialIcon.BUG_REPORT)
    }

    private val name: TextView by ViewContainer(this, "name") { TextView(robot.name, "Unnamed") }
    private val type: TextView by ViewContainer(this, "type") { TextView(robot.type) }

    init {
        robot.nameProperty.onChange { newValue, _ ->
            name.text = newValue
        }
        robot.typeProperty.onChange { newValue, _ ->
            type.text = newValue
        }

        click.on {
            it.stopPropagation()

            if (onClickListener()) return@on

            ContextMenu(((it as? MouseEvent)?.clientX ?: 0) to ((it as? MouseEvent)?.clientY ?: 0)) {
                item("Open robot details") {
                    Router.routeTo("admin/robots/${robot.id}")
                }
                item("Remove robot") {
                    WebSocketConnection.iServer.unbind(controller.id, robot.id)
                }
                open()
            }
        }
    }


}