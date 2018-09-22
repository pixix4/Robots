package de.westermann.robots.datamodel.observe

import kotlin.reflect.KProperty

/**
 * @author lars
 */
class ObservableDelegate<T>(
        private val observableProperty: ObservableProperty<T>
) {
    operator fun getValue(any: Any, property: KProperty<*>) = this.observableProperty.value

    operator fun setValue(any: Any, property: KProperty<*>, t: T) {
        this.observableProperty.value = t
    }
}