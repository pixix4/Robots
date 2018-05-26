package de.westermann.robots.website.toolkit.view

import org.w3c.dom.Element

/**
 * @author lars
 */

open class SelectableViewList<T : View>(
        private val multiple: Boolean = false
) : ViewList<T>() {

    private val selected = mutableListOf<T>()

    private val selectListener = mutableMapOf<T, MutableList<(T) -> Unit>>()

    fun bind(element: T, onSelect: (elem: T) -> Unit) {
        selectListener.getOrPut(element, {
            mutableListOf()
        }).add(onSelect)
    }

    fun selectionDown() {
        if (multiple) return

        val index = (selected.getOrNull(0)?.let {
            children.map { it.first }.indexOf(it) - 1
        } ?: -1).let {
            if (it < 0) children.size - 1 else it
        }

        select(children[index].first)
    }

    fun selectionUp() {
        if (multiple) return

        val index = (selected.getOrNull(0)?.let {
            children.map { it.first }.indexOf(it) + 1
        } ?: children.size).let {
            if (it >= children.size) 0 else it
        }

        select(children[index].first)
    }

    fun selectionExec() {
        if (multiple) return
        val element = selected.getOrNull(0) ?: return

        selectListener[element]?.forEach { it(element) }
    }

    fun select(child: T, trigger: Boolean = true) {
        if (!multiple) selected.forEach { unselect(it) }

        children.find { it.first == child }?.also {
            it.second.classList.add(CSS)
            selected.add(child)
            if (trigger) {
                selectListener[child]?.forEach { it(child) }
            }
        }
    }

    fun unselect(child: T) {
        if (child !in selected)
            return

        children.find { it.first == child }?.also {
            it.second.classList.remove(CSS)
            selected.remove(child)
        }
    }

    override fun createContainer(): Element =
            super.createContainer().also {
                it.addEventListener("click", { _ ->
                    children.find { elem -> elem.second == it }?.let {
                        select(it.first)
                    }
                })
            }

    companion object {
        private const val CSS: String = "active"
    }

}