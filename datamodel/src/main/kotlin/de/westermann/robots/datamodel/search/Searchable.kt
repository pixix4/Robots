package de.westermann.robots.datamodel.search

/**
 * @author lars
 */
interface Searchable {
    fun probability(search: String): Double
}