package de.westermann.robots.datamodel.observe

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

    override fun iterator(): Iterator<T> = list.keys.toList().iterator()

    operator fun get(id: Int): T? = list.keys.find { it.id == id }

    val nextId: Int
        get() = list.map { it.key.id }.toSet().let {
            ((0..(it.max() ?: 0)).toSet() - it).min() ?: 0
        }

    interface Observer<T> {
        fun onAdd(element: T) {}
        fun onChange(element: T) {}
        fun onRemove(element: T) {}
    }
}