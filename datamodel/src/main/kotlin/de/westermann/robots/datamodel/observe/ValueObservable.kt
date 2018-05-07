package de.westermann.robots.datamodel.observe

/**
 * @author lars
 */
abstract class ValueObservable<T> {

    private var observers = listOf<Observer<T>>()

    fun addObserver(observer: Observer<T>) {
        observers += observer
    }

    fun removeObserver(observer: Observer<T>) {
        observers -= observer
    }

    fun clearObservers() {
        observers = emptyList()
    }

    protected fun notify(newValue: T, oldValue: T) {
        observers.forEach { it(newValue, oldValue) }
    }

}