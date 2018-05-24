package de.westermann.robots.website.toolkit.view

import de.westermann.robots.datamodel.observe.EventHandler
import kotlin.browser.window
import kotlin.js.Date

/**
 * @author lars
 */
class TimeoutEventHandler<Event : Any>(
        val minChangeTimeout: Int = 0
): EventHandler<Event>() {

    constructor(minChangeTimeout: Int = 0, onEvent: (Event) -> Unit) : this(minChangeTimeout) {
        addListener(onEvent)
    }


    private var lastSend: Int = 0
    private lateinit var lastEvent: Event
    private var pendingTimeout: Boolean = false

    override fun fire(event: Event) {
        if (minChangeTimeout == 0) {
            super.fire(event)
        } else {
            val now = Date.now().toInt()
            val elapsed = now - lastSend

            if (elapsed >= minChangeTimeout) {
                super.fire(event)

                lastSend = now
            } else {
                lastEvent = event

                if (!pendingTimeout) {
                    pendingTimeout = true
                    window.setTimeout({
                        super.fire(event)

                        pendingTimeout = false
                    }, minChangeTimeout - elapsed)
                }
            }
        }
    }
}