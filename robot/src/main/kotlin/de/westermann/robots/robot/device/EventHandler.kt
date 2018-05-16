package de.westermann.robots.robot.device

/**
 * @author lars
 */
class EventHandler<Event>() {

    constructor(onEvent: (Event) -> Unit) : this() {
        addListener(onEvent)
    }

    private val listener = mutableListOf<(Event) -> Unit>()

    fun addListener(event: (Event) -> Unit) {
        listener.add(event)
    }

    fun removeListener(on: (Event) -> Unit) {
        listener.remove(on)
    }

    fun clearListener() {
        listener.clear()
    }

    fun on(event: (Event) -> Unit) = addListener(event)

    fun fire(event: Event) {
        listener.forEach {
            it(event)
        }
    }
}