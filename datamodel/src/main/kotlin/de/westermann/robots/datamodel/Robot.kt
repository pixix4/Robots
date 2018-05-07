package de.westermann.robots.datamodel

import de.westermann.robots.datamodel.observe.ObjectObservable
import de.westermann.robots.datamodel.observe.ObservableProperty
import de.westermann.robots.datamodel.observe.accessor
import de.westermann.robots.datamodel.util.*

/**
 * @author lars
 */
class Robot : ObjectObservable() {

    val nameProperty = "".observable()
    var name by nameProperty.accessor()

    val typeProperty: ObservableProperty<String?> = null.observable()
    var type by typeProperty.accessor()

    val versionProperty = Version.UNKNOWN.observable()
    var version by versionProperty.accessor()

    val colorProperty = Color.NONE.observable()
    var color by colorProperty.accessor()

    val speedProperty = (-1.0).observable()
    var speed by speedProperty.accessor()

    val trackProperty = Track.DEFAULT.observable()
    var track by trackProperty.accessor()

    val lineFollowerPropety = LineFollower.UNKNOWN.observable()
    var lineFollower by lineFollowerPropety.accessor()

    val cameraProperty = Camera.UNKNOWN.observable()
    var camera by cameraProperty.accessor()

    val energyProperty = Energy.UNKNOWN.observable()
    var energy by energyProperty.accessor()

    val controllers: Set<Controller> = DeviceManager.getBoundControllers(this)
}