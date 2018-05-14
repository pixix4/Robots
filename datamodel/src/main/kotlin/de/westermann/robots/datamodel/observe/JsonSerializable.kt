package de.westermann.robots.datamodel.observe

import de.westermann.robots.datamodel.util.Json

/**
 * @author lars
 */
interface JsonSerializable {
    fun toJson(): Json
    fun fromJson(json: Json)
}