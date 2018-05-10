package de.westermann.robots.datamodel.observe

/**
 * @author lars
 */
class ObservableFunction<T>(
        private val getter: () -> T
): ObservableProperty<T>(getter()) {

    override var value: T
        get() = getter()
        set(value) {
            super.value = value
        }

    fun update() {
        value = getter()
    }
}