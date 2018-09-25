package de.westermann.robots.website.component

import de.westermann.robots.datamodel.DeviceManager
import de.westermann.robots.datamodel.Robot
import de.westermann.robots.datamodel.observe.Library
import de.westermann.robots.website.toolkit.Router
import de.westermann.robots.website.toolkit.widget.CardView

/**
 * @author lars
 */
fun Router.adminRobotList() {
    view {
        CardView<RobotCard> {
            var robots: Map<Robot, RobotCard> = emptyMap()

            fun addRobot(robot: Robot) {
                val card = RobotCard(robot)
                robots += robot to card
                this += card
            }

            fun removeRobot(robot: Robot) {
                robots[robot]?.let { card ->
                    this -= card
                }
            }

            DeviceManager.robots.onChange(object : Library.Observer<Robot> {
                override fun onAdd(element: Robot) {
                    addRobot(element)
                }

                override fun onRemove(element: Robot) {
                    removeRobot(element)
                }
            })

            DeviceManager.robots.forEach(::addRobot)
        }
    }
}