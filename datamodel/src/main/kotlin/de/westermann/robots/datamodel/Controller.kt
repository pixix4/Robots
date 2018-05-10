package de.westermann.robots.datamodel

import de.westermann.robots.datamodel.observe.ObservableObject
import de.westermann.robots.datamodel.observe.accessor
import de.westermann.robots.datamodel.util.Color

/**
 * @author lars
 */
class Controller(
        override val id: Int
) : ObservableObject() {

    constructor(id: Int, init: Controller.() -> Unit) : this(id) {
        init()
    }

    val nameProperty = "".observable()
    var name by nameProperty.accessor()

    val codeProperty = 0.observable()
    var code by codeProperty.accessor()

    val typeProperty = Type.UNKNOWN.observable()
    var type by typeProperty.accessor()

    val descriptionProperty = "".observable()
    var description by descriptionProperty.accessor()

    val colorProperty = Color.TRANSPARENT.observable()
    var color by colorProperty.accessor()

    override fun <T> update(element: T): Boolean =
            (element as? Controller)?.let {
                var changed = false
                if (type == it.type) {
                    type = it.type
                    changed = true
                }
                if (code == it.code) {
                    code = it.code
                    changed = true
                }
                if (color == it.color) {
                    color = it.color
                    changed = true
                }
                if (type == it.type) {
                    type = it.type
                    changed = true
                }
                if (description == it.description) {
                    description = it.description
                    changed = true
                }
                changed
            } ?: false

    val robotsProperty = {
        DeviceManager.getBoundRobots(this)
    }.observableFunction()
    val robots by robotsProperty.accessor()

    enum class Type {
        DESKTOP, MOBIL, PHYSICAL, UNKNOWN
    }
}