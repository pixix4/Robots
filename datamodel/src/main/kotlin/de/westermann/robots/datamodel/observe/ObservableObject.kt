package de.westermann.robots.datamodel.observe

/**
 * @author lars
 */

abstract class ObservableObject : JsonSerializable {

    abstract val id: Int

    private var observers = listOf<() -> Unit>()

    fun addObserver(observer: () -> Unit): () -> Unit {
        observers += observer
        return observer
    }

    fun removeObserver(observer: () -> Unit) {
        observers -= observer
    }

    fun clearObservers() {
        observers = emptyList()
    }

    fun notifyObservers() {
        observers.forEach { it() }
    }

    fun <T> T.observable(notifyObject: Boolean = true) = ObservableProperty(this).apply {
        if (notifyObject) {
            addObserver({ _, _ -> notifyObservers() })
        }
    }

    fun <T> (() -> T).observableFunction(notifyObject: Boolean = false) = ObservableFunction(this).apply {
        if (notifyObject) {
            addObserver({ _, _ -> notifyObservers() })
        }
    }
}