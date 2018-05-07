package de.westermann.robots.datamodel.observe

/**
 * @author lars
 */

abstract class ObjectObservable {

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

    fun notify() {
        observers.forEach { it() }
    }

    fun <T> T.observable() = this.createObservable { _, _ -> notify() }
}