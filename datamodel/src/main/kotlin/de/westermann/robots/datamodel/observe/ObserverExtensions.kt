package de.westermann.robots.datamodel.observe

/**
 * @author lars
 */

typealias Observer<T> = (newValue: T, oldValue: T) -> Unit


fun <T> ObservableProperty<T>.accessor() = ObservableDelegate(this)
