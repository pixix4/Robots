package de.westermann.robots.datamodel.observe

/**
 * @author lars
 */
class Library<T : ObjectObservable> : Iterable<T> {

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

    fun add(element: T) {
        if (list.containsKey(element))
            return
        list += element to {
            notifyChange(element)
        }.also {
            element.addObserver(it)
        }
        notifyAdd(element)
    }

    operator fun plusAssign(element: T) = add(element)

    fun remove(element: T) {
        if (!list.containsKey(element))
            return
        list[element]?.let {
            element.removeObserver(it)
        }
        list -= element
        notifyRemove(element)
    }

    operator fun minusAssign(element: T) = remove(element)

    override fun iterator(): Iterator<T> = list.keys.iterator()

    interface Observer<T> {
        fun onAdd(element: T)
        fun onChange(element: T)
        fun onRemove(element: T)
    }
}