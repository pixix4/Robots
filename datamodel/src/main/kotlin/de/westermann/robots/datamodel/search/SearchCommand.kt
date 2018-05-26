package de.westermann.robots.datamodel.search

/**
 * @author lars
 */
enum class SearchCommand(val value: List<Pair<String, Int>> = emptyList()) {
    BIND(listOf(
            "bind" to 10,
            "assign" to 8,
            "to" to 4,
            "add" to 1
    )),
    UNBIND(listOf(
            "unbind" to 10,
            "deassign" to 8,
            "remove" to 4,
            "delete" to 4,
            "from" to 1
    )),
    UNKNOWN;

    fun probability(word: String): Int = value.find { it.first == word }?.second ?: 0

    companion object {
        val lookup: Map<String, Pair<SearchCommand, Int>> = SearchCommand.values().flatMap { command ->
            command.value.map {
                it.first to Pair(command, it.second)
            }
        }.toMap()
    }
}