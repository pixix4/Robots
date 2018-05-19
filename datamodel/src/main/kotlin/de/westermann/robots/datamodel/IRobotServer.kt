package de.westermann.robots.datamodel

import de.westermann.robots.datamodel.util.Color
import de.westermann.robots.datamodel.util.Coordinate
import de.westermann.robots.datamodel.util.Energy
import de.westermann.robots.datamodel.util.Version

/**
 * @author lars
 */
interface IRobotServer {
    fun map(points: List<Coordinate>)

    fun currentColor(color: Color)
    fun foregroundColor(color: Color)
    fun backgroundColor(color: Color)

    fun energy(energy: Energy)
    fun version(version: Version)
}