package de.westermann.robots.website.component

import de.westermann.robots.datamodel.Controller
import de.westermann.robots.datamodel.DeviceManager
import de.westermann.robots.datamodel.observe.Library
import de.westermann.robots.website.WebSocketConnection
import de.westermann.robots.website.toolkit.Router
import de.westermann.robots.website.toolkit.icon.MaterialIcon
import de.westermann.robots.website.toolkit.view.View
import de.westermann.robots.website.toolkit.view.ViewContainer
import de.westermann.robots.website.toolkit.view.ViewList
import de.westermann.robots.website.toolkit.widget.*

/**
 * @author lars
 */
class ControllerDetail(controller: Controller) : View() {

    private val topBox: Box by ViewContainer(this, "top") {
        Box {}
    }

    @Suppress("UNUSED")
    private val icon: IconView by ViewContainer(this, "icon") {
        controller.colorProperty.onChangeInit { newValue, _ ->
            it.style.backgroundColor = newValue.lightness(0.8).toString()
        }
        IconView()
    }

    private val name: TextView by ViewContainer(this, "name", topBox.element) {
        TextView(controller.name, "Unnamed") {
            editable = true
            edit.on {
                WebSocketConnection.iServer.setControllerName(controller.id, it)
            }
        }
    }

    private val code: TextView by ViewContainer(this, "code", topBox.element) {
        TextView()
    }


    private val robots = ViewList<RobotListItem>().also {
        it.element.classList.add("controller-detail-robots")
        it.footer = Action(
                "Add robot",
                MaterialIcon.ADD
        ) {
            click.on {
                addRobotDialog(controller).show()
            }
        }
    }

    @Suppress("UNUSED")
    private val contentBox: CardView<View> by ViewContainer(this, "content") {
        CardView<View> {
            hoverHighlight = false
            box {
                textView("Controllers")
                +robots
            }
        }
    }


    private val removeListener = object : Library.Observer<Controller> {
        override fun onRemove(element: Controller) {
            if (element == controller) {
                remove()
            }
        }
    }

    private fun remove() {
        Router.routeUp()
        DeviceManager.controllers.onChange(removeListener)
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
                robots += RobotListItem(it, controller)
            }
        }

        DeviceManager.controllers.onChange(removeListener)
    }
}