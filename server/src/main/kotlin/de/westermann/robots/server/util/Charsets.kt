package de.westermann.robots.server.util

/**
 * @author lars
 */
object Charsets {
    fun charsetsToList(charsets: String): Set<Char> {
        var charArray = charsets.toCharArray().toList()
        var list = emptySet<Char>()
        while (charArray.isNotEmpty()) {
            val first = charArray.first()
            charArray = charArray.drop(1)

            if (charArray.first() == '-') {
                val last = charArray[1]
                charArray = charArray.drop(2)

                list += first..last
            } else {
                list += first
            }
        }
        return list
    }
}