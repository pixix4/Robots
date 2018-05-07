package de.westermann.robots.datamodel.observe

/**
 * @author lars
 */
class ObservableProperty<T>(
        value: T
) : ValueObservable<T>() {
    var value = value
        set(value) {
            val oldValue = value
            field = value
            notify(value, oldValue)
        }
}