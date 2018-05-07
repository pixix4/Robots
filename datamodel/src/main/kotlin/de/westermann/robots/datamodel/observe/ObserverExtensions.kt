package de.westermann.robots.datamodel.observe

/**
 * @author lars
 */

typealias Observer<T> = (newValue: T, oldValue: T) -> Unit


fun <T> ObservableProperty<T>.accessor() = ObservableDelegate(this)

fun <T> T.createObservable(initialObserver: Observer<T>) = ObservableProperty(this).apply {
    addObserver(initialObserver)
}