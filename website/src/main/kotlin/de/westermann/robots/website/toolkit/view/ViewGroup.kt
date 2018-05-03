package de.westermann.robots.website.toolkit.view

/**
 * @author lars
 */

abstract class ViewGroup : View() {
    protected fun setupContainer(container:ViewContainer<*, *>) {
        container.putToElement(element)
    }
}