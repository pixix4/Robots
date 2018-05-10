package de.westermann.robots.website.toolkit.widget

import de.westermann.robots.website.toolkit.view.View
import org.w3c.dom.HTMLImageElement
import kotlin.browser.document

/**
 * @author lars
 */

class ImageView(source: String = "", init: ImageView.() -> Unit = {}) : View() {


    private val imageElement: HTMLImageElement = (document.createElement("img") as HTMLImageElement).also {
        element.appendChild(it)
    }

    var source: String = ""
        set(value) {
            field = value
            imageElement.src = value
        }

    init {
        this.source = source
        init()
    }
}

