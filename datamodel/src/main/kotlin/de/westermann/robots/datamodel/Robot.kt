package de.westermann.robots.datamodel

import de.westermann.robots.datamodel.observe.EventHandler
import de.westermann.robots.datamodel.observe.ObservableObject
import de.westermann.robots.datamodel.observe.accessor
import de.westermann.robots.datamodel.search.StringSimilarity
import de.westermann.robots.datamodel.util.*

/**
 * @author lars
 */
class Robot(
        override val id: Int
) : ObservableObject() {

    constructor(id: Int, init: Robot.() -> Unit) : this(id) {
        init(this)
    }

    constructor(init: Robot.() -> Unit) : this(DeviceManager.robots.nextId) {
        init(this)
    }

    val nameProperty = "".observable()
    var name by nameProperty.accessor()

    val typeProperty = "".observable()
    var type by typeProperty.accessor()

    val versionProperty = Version.UNKNOWN.observable()
    var version by versionProperty.accessor()

    val visibleColorProperty = Color.TRANSPARENT.observable()
    var visibleColor by visibleColorProperty.accessor()

    val colorProperty = Color.TRANSPARENT.observable()
    var color by colorProperty.accessor()

    val availableColorsProperty = emptyList<Color>().observable()
    var availableColors by availableColorsProperty.accessor()

    val speedProperty = 0.5.observable()
    var speed by speedProperty.accessor()

    val trimProperty = 0.0.observable()
    var trim by trimProperty.accessor()

    val trackProperty = Track.DEFAULT.observable(false)
    var track by trackProperty.accessor()

    val lineFollowerPropety = LineFollower.UNKNOWN.observable()
    var lineFollower by lineFollowerPropety.accessor()

    val energyProperty = Energy.UNKNOWN.observable()
    var energy by energyProperty.accessor()

    val cameraProperty = Camera.NONE.observable()
    var camera by cameraProperty.accessor()

    val kickerProperty = Kicker.NONE.observable()
    var kicker by kickerProperty.accessor()

    val mapProperty = emptyList<Coordinate>().observable()
    var map by mapProperty.accessor()

    val button = EventHandler<Button>()
    val setForegroundColor = EventHandler<Unit>()
    val setBackgroundColor = EventHandler<Unit>()

    val controllersProperty = {
        DeviceManager.getBoundControllers(this)
    }.observableFunction()
    val controllers by controllersProperty.accessor()

    var iRobotServer: IRobotServer? = null

    override fun toString(): String = "Robot($id: '$name')"

    override fun toJson(): Json = json {
        value("id") { id }
        value("name") { name }
        value("type") { type }
        value("version") { version.toJson() }
        value("visibleColor") { visibleColor.toString() }
        value("color") { color.toString() }
        value("availableColors") { availableColors.joinToString(";") }
        value("speed") { speed }
        value("trim") { trim }
        value("track") { track.toJson() }
        value("lineFollower") { lineFollower.toJson() }
        value("energy") { energy.toJson() }
        value("camera") { camera.toJson() }
        value("kicker") { kicker.toJson() }
    }

    override fun fromJson(json: Json) {
        json["name"]?.toString()?.let { name = it }
        json["type"]?.toString()?.let { type = it }
        json.json("version")?.let { version = Version.fromJson(it) }
        json["visibleColor"]?.toString()?.let {
            try {
                visibleColor = Color.parse(it)
            } catch (_: IllegalArgumentException) {
            }
        }
        json["color"]?.toString()?.let {
            try {
                color = Color.parse(it)
            } catch (_: IllegalArgumentException) {
            }
        }
        json["availableColors"]?.toString()?.let {
            availableColors = it.split(";").map {
                try {
                    Color.parse(it)
                } catch (_: IllegalArgumentException) {
                    null
                }
            }.filterNotNull()
        }
        json["speed"]?.toString()?.toDoubleOrNull()?.let { speed = it }
        json["trim"]?.toString()?.toDoubleOrNull()?.let { trim = it }
        json.json("track")?.let { track = Track.fromJson(it) }
        json.json("lineFollower")?.let { lineFollower = LineFollower.fromJson(it) }
        json.json("energy")?.let { energy = Energy.fromJson(it) }
        json.json("camera")?.let { camera = Camera.fromJson(it) }
        json.json("kicker")?.let { kicker = Kicker.fromJson(it) }
    }

    override fun probability(search: String): Double = StringSimilarity.check(
            search,
            name to 1.0,
            type to 0.8,
            id.toString() to 0.6
    )

    companion object {
        fun fromJson(json: Json) = Robot(
                json["id"]?.toString()?.toIntOrNull() ?: 0
        ).also { it.fromJson(json) }
    }
}