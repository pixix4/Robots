package de.westermann.robots.datamodel.util

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser


/**
 * @author lars
 */
actual class Json(
        val obj: JsonObject
) {
    actual operator fun get(propertyName: String): Any? = obj[propertyName]

    actual operator fun set(propertyName: String, value: Any?) {
        obj[propertyName] = value
    }

    actual fun value(propertyName: String, init: () -> Any?) = set(propertyName, init())
    actual fun json(propertyName: String): Json? = (get(propertyName) as? JsonObject)?.let { Json(it) }

    actual fun json(propertyName: String, init: Json.() -> Unit) = set(propertyName, create(init))

    actual fun stringify(): String = obj.toJsonString(false)

    override fun toString(): String = stringify()

    actual companion object {
        actual fun create(init: Json.() -> Unit): Json = Json(JsonObject()).also(init)

        actual fun fromString(data: String): Json =
                Json(Parser().parse(StringBuilder(data)) as JsonObject)

    }

}