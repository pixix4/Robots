package de.westermann.robots.website.component

import de.westermann.robots.datamodel.Robot
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
class RobotCardMinimal(robot: Robot) : View() {

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
            println("Open robot context menu: $robot")
        }
    }

}