package de.westermann.robots.datamodel.observe

/**
 * @author lars
 */
open class ObservableProperty<T>(
        value: T
) {
    open var value = value
        set(value) {
            if (value == field)
                return

            val oldValue = field
            field = value
            notifyObservers(value, oldValue)
        }

    private var observers = listOf<Observer<T>>()

    fun onChange(observer: Observer<T>) = addObserver(observer)

    fun onChangeInit(observer: Observer<T>) {
        addObserver(observer)
        observer(value, value)
    }

    fun addObserver(observer: Observer<T>) {
        observers += observer
    }

    fun removeObserver(observer: Observer<T>) {
        observers -= observer
    }

    fun clearObservers() {
        observers = emptyList()
    }

    private fun notifyObservers(newValue: T, oldValue: T) {
        observers.forEach { it(newValue, oldValue) }
    }

}