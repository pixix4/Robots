package de.westermann.robots.datamodel.observe

import de.westermann.robots.datamodel.search.Match

/**
 * @author lars
 */
class Library<T : ObservableObject> : Iterable<T> {

    private var list = mapOf<T, () -> Unit>()
    private var observers = setOf<Observer<T>>()

    private fun notifyAdd(element: T) {
        observers.forEach { it.onAdd(element) }
    }

    private fun notifyChange(element: T) {
        observers.forEach { it.onChange(element) }
    }

    private fun notifyRemove(element: T) {
        observers.forEach { it.onRemove(element) }
    }

    fun onChange(observer: Observer<T>) {
        observers += observer
    }

    fun removeObserver(observer: Observer<T>) {
        observers -= observer
    }

    fun clearObservers() {
        observers = emptySet()
    }

    fun add(element: T) {
        if (list.containsKey(element))
            return

        if (get(element.id) == null) {
            list += element to {
                notifyChange(element)
            }.also {
                element.addObserver(it)
            }

            notifyAdd(element)
        }
    }

    operator fun plusAssign(element: T) = add(element)

    fun remove(element: T) {
        if (!list.containsKey(element) || get(element.id) == null)
            return
        list[element]?.let {
            element.removeObserver(it)
        }
        list -= element
        notifyRemove(element)
    }

    operator fun minusAssign(element: T) = remove(element)
    operator fun minusAssign(id: Int) {
        get(id)?.let { remove(it) }
    }

    fun clear() {
        list = emptyMap()
    }

    override fun iterator(): Iterator<T> = list.keys.toList().iterator()

    operator fun get(id: Int): T? = list.keys.find { it.id == id }

    @Suppress("UNUSED")
    val nextUnusedId: Int
        get() = list.map { it.key.id }.toSet().let {
            ((0..(it.max() ?: 0)).toSet() - it).min() ?: it.max()?.let { it + 1 } ?: 0
        }

    private var lastIndex = -1
    val nextId: Int
        get() {
            lastIndex += 1
            return lastIndex
        }


    fun toSet(): Set<T> = list.keys
    fun toList(): List<T> = toSet().toList()

    fun find(search: List<String>, minimalProbability: Double = 0.0, limit: Int = 0): List<Match<T>> =
            toSet().map { elem ->
                Match(elem, search.map {
                    it to elem.probability(it)
                }.toMap().filterValues { it > minimalProbability })
            }.filter {
                it.probability > minimalProbability
            }.sortedDescending().let {
                if (limit > 0) {
                    it.take(limit)
                } else it
            }

    interface Observer<T> {
        fun onAdd(element: T) {}
        fun onChange(element: T) {}
        fun onRemove(element: T) {}
    }
}