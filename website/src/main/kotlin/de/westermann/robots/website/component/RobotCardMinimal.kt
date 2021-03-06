package de.westermann.robots.website.component

import de.westermann.robots.datamodel.Controller
import de.westermann.robots.datamodel.Robot
import de.westermann.robots.website.WebSocketConnection
import de.westermann.robots.website.toolkit.Router
import de.westermann.robots.website.toolkit.view.View
import de.westermann.robots.website.toolkit.view.ViewContainer
import de.westermann.robots.website.toolkit.widget.ContextMenu
import de.westermann.robots.website.toolkit.widget.ImageView
import de.westermann.robots.website.toolkit.widget.TextView
import org.w3c.dom.events.MouseEvent

/**
 * @author lars
 */
class RobotCardMinimal(robot: Robot, controller: Controller) : View() {

    private val image: ImageView by ViewContainer(this, "image") {
        //robot.colorProperty.onChangeInit { newValue, _ ->
        //    it.style.backgroundColor = newValue.lightness(0.8).stringify()
        //}
        ImageView("/public/images/robot.png")
    }

    private val name: TextView by ViewContainer(this, "name") { TextView(robot.name, "Unnamed") }

    init {
        robot.nameProperty.onChange { newValue, _ ->
            name.text = newValue
        }

        click.on {
            it.stopPropagation()

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