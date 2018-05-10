package de.westermann.robots.datamodel.observe

/**
 * @author lars
 */

abstract class ObservableObject {

    abstract val id: Int

    private var observers = listOf<() -> Unit>()

    fun addObserver(observer: () -> Unit) {
        observers += observer
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

    fun <T> T.observable() = ObservableProperty(this).apply {
        addObserver({ _, _ -> notifyObservers() })
    }


    fun <T> (() -> T).observableFunction() = ObservableFunction(this).apply {
        addObserver({ _, _ -> notifyObservers() })
    }

    abstract fun <T> update(element: T): Boolean
}