package de.westermann.robots.website.toolkit.view

import kotlin.browser.window
import kotlin.js.Date

/**
 * @author lars
 */
class EventHandler<Event : Any>(
        val minChangeTimeout: Int = 0
) {

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

    var lastSend: Int = 0
    lateinit var lastEvent: Event
    var pendingTimeout: Boolean = false

    fun fire(event: Event) {
        if (minChangeTimeout == 0) {
            listener.forEach {
                it(event)
            }
        } else {
            val now = Date.now().toInt()
            val elapsed = now - lastSend

            if (elapsed >= minChangeTimeout) {
                listener.forEach {
                    it(event)
                }

                lastSend = now
            } else {
                lastEvent = event

                if (!pendingTimeout) {
                    pendingTimeout = true
                    window.setTimeout({
                        listener.forEach {
                            it(lastEvent)
                        }
                        pendingTimeout = false
                    }, minChangeTimeout - elapsed)
                }
            }
        }
    }
}