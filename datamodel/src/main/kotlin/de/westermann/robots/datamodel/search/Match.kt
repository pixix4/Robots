package de.westermann.robots.datamodel.search

/**
 * @author lars
 */
data class Match<T : Searchable>(
        val element: T,
        val matches: Map<String, Double>
) : Comparable<Match<T>> {

    val probability: Double = matches.values.max() ?: 0.0

    override fun compareTo(other: Match<T>): Int = probability.compareTo(other.probability)
}