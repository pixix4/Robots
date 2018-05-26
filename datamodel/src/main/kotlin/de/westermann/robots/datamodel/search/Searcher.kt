package de.westermann.robots.datamodel.search

import de.westermann.robots.datamodel.Controller
import de.westermann.robots.datamodel.DeviceManager
import de.westermann.robots.datamodel.Robot

/**
 * @author lars
 */
class Searcher(
        rawInput: String,
        limit: Int = 0
) {
    val input: String
    val command: SearchCommand

    val robots: List<Match<Robot>>
    val controllers: List<Match<Controller>>

    init {
        input = rawInput.toLowerCase().trim().replace(" +".toRegex(), " ")
        val words = input.split(" ")
        var found: List<Pair<SearchCommand, Int>> = words.foldIndexed(emptyList()) { index, acc, word ->
            SearchCommand.lookup[word]?.let {
                acc + (it.first to index)
            } ?: acc
        }

        command = found.maxBy { it.first.probability(words[it.second]) }?.let {
            if (it.first.probability(words[it.second]) > 0)
                it.first
            else
                null
        } ?: SearchCommand.UNKNOWN

        val lookupGroups = words.foldIndexed(emptyList<List<String>>()) { index, acc, word ->
            if (found.isEmpty() || index != found.first().second) {
                acc.dropLast(1).plusElement((acc.lastOrNull() ?: emptyList()) + word)
            } else {
                found = found.drop(1)
                acc.plusElement(emptyList())
            }
        }.filter { it.isNotEmpty() }.map {
            it.joinToString(" ")
        }

        robots = DeviceManager.robots.find(lookupGroups, MINIMAL_PROBABILITY, limit)
        controllers = DeviceManager.controllers.find(lookupGroups, MINIMAL_PROBABILITY, limit)
    }

    companion object {
        const val MINIMAL_PROBABILITY = 0.2
    }
}