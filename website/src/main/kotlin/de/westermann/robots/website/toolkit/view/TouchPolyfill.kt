package de.westermann.robots.website.toolkit.view

import org.w3c.dom.events.EventTarget
import org.w3c.dom.events.UIEvent

/**
 * @author lars
 */

open external class TouchEvent(type: String) : UIEvent {
    open val changedTouches: TouchList
    open val targetTouches: TouchList
    open val touches: TouchList
    open val ctrlKey: Boolean
    open val shiftKey: Boolean
    open val altKey: Boolean
    open val metaKey: Boolean
    fun getModifierState(keyArg: String): Boolean
}

open external class TouchList() {
    open val length: Int
    open fun item(index: Int): Touch?
}

open external class Touch() {
    open val identifier: Int
    open val target: EventTarget
    open val screenX: Int
    open val screenY: Int
    open val clientX: Int
    open val clientY: Int
    open val pageX: Int
    open val pageY: Int
}

operator fun TouchList.get(index: Int) = item(index)
fun TouchList.all(): List<Touch> = (0..length).map { item(it) }.filterNotNull()
fun TouchList.find(identifier: Int): Touch? = all().find { it.identifier == identifier }