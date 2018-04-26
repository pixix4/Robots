package de.westermann.robots.server.utils

import kotlin.math.max

/**
 * @author lars
 */
sealed class Printer {

    protected abstract fun internalLog(logger: (String) -> Unit, indent: Int)
    protected abstract fun internalLog(logger: (String) -> Unit, indent: Int, colWidths: List<Int>)
    protected abstract val colWidths: List<Int>

    fun log(logger: (String) -> Unit) = internalLog(logger, 0)

    class Table(
            private val title: String
    ) : Printer() {
        private val children: MutableList<Printer> = mutableListOf()

        constructor(title: String, children: List<Printer>) : this(title) {
            this.children.addAll(children)
        }

        override val colWidths: List<Int>
            get() = children.map { it.colWidths }.fold(emptyList()) { acc, list ->
                (0 until maxOf(acc.size, list.size)).map {
                    max(acc.getOrElse(it, { 0 }), list.getOrElse(it, { 0 }))
                }
            }

        override fun internalLog(logger: (String) -> Unit, indent: Int) = internalLog(
                logger,
                indent,
                colWidths
        )

        override fun internalLog(logger: (String) -> Unit, indent: Int, colWidths: List<Int>) {
            if (title.isNotBlank()) {
                logger("${Environment.INDENT.repeat(indent)}$title")
            }
            val childrenIndent = indent + 1
            for (printer in children) {
                when (printer) {
                    is Table -> printer.internalLog(logger, childrenIndent, colWidths)
                    is Line -> printer.internalLog(logger, childrenIndent, colWidths)
                }
            }
        }
    }

    class Line(
            private val columns: List<String>
    ) : Printer() {

        constructor(vararg cols: String) : this(cols.toList())

        override val colWidths: List<Int> = columns.map { it.length }

        override fun internalLog(logger: (String) -> Unit, indent: Int, colWidths: List<Int>) {
            logger(
                    "${Environment.INDENT.repeat(indent)}${
                    columns.zip(colWidths).map { (value, length) ->
                        value.padEnd(length, ' ')
                    }.let {
                        it.dropLast(1) + it.last().trim()
                    }.joinToString(Environment.INDENT)
                    }"
            )
        }

        override fun internalLog(logger: (String) -> Unit, indent: Int) =
                internalLog(logger, indent, colWidths)

    }
}