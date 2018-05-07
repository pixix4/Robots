package de.westermann.robots.datamodel

import de.westermann.robots.datamodel.observe.ObjectObservable
import de.westermann.robots.datamodel.observe.accessor
import de.westermann.robots.datamodel.util.Version

/**
 * @author lars
 */
class Controller : ObjectObservable() {

    val nameProperty = "".observable()
    var name by nameProperty.accessor()

    val codeProperty = 0.observable()
    var code by codeProperty.accessor()

    val typeProperty = Type.UNKNOWN.observable()
    var type by typeProperty.accessor()

    val descriptionProperty = "".observable()
    var description by descriptionProperty.accessor()

    val robots: Set<Robot> = DeviceManager.getBoundRobots(this)

    enum class Type {
        DESKTOP, MOBIL, PHYSICAL, UNKNOWN
    }
}