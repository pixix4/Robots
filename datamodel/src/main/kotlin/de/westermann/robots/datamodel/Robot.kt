package de.westermann.robots.datamodel

import de.westermann.robots.datamodel.observe.ObservableObject
import de.westermann.robots.datamodel.observe.ObservableProperty
import de.westermann.robots.datamodel.observe.accessor
import de.westermann.robots.datamodel.util.*

/**
 * @author lars
 */
class Robot(
        override val id: Int
) : ObservableObject() {

    constructor(id: Int, init: Robot.() -> Unit) : this(id) {
        init()
    }

    val nameProperty = "".observable()
    var name by nameProperty.accessor()

    val typeProperty: ObservableProperty<String?> = null.observable()
    var type by typeProperty.accessor()

    val versionProperty = Version.UNKNOWN.observable()
    var version by versionProperty.accessor()

    val colorProperty = Color.TRANSPARENT.observable()
    var color by colorProperty.accessor()

    val speedProperty = (-1.0).observable()
    var speed by speedProperty.accessor()

    val trackProperty = Track.DEFAULT.observable()
    var track by trackProperty.accessor()

    val lineFollowerPropety = LineFollower.UNKNOWN.observable()
    var lineFollower by lineFollowerPropety.accessor()

    val energyProperty = Energy.UNKNOWN.observable()
    var energy by energyProperty.accessor()

    val cameraProperty = Camera.NONE.observable()
    var camera by cameraProperty.accessor()

    val kickerProperty = Kicker.NONE.observable()
    var kicker by kickerProperty.accessor()

    override fun <T> update(element: T): Boolean =
            (element as? Robot)?.let {
                var changed = false
                if (type == it.type) {
                    type = it.type
                    changed = true
                }
                if (version == it.version) {
                    version = it.version
                    changed = true
                }
                if (color == it.color) {
                    color = it.color
                    changed = true
                }
                if (speed == it.speed) {
                    speed = it.speed
                    changed = true
                }
                if (track == it.track) {
                    track = it.track
                    changed = true
                }
                if (lineFollower == it.lineFollower) {
                    lineFollower = it.lineFollower
                    changed = true
                }
                if (energy == it.energy) {
                    energy = it.energy
                    changed = true
                }
                if (camera == it.camera) {
                    camera = it.camera
                    changed = true
                }
                if (kicker == it.kicker) {
                    kicker = it.kicker
                    changed = true
                }
                changed
            } ?: false

    val controllersProperty = {
        DeviceManager.getBoundControllers(this)
    }.observableFunction()
    val controllers by controllersProperty.accessor()
}