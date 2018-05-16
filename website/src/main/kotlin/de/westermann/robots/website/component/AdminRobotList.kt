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
            fun reload() {
                clear()
                DeviceManager.robots.forEach {
                    this += RobotCard(it)
                }
            }
            DeviceManager.robots.onChange(object : Library.Observer<Robot> {
                override fun onAdd(element: Robot) {
                    this@CardView += RobotCard(element)
                }

                override fun onRemove(element: Robot) {
                    reload()
                }
            })
            reload()
        }
    }
}