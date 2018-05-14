package de.westermann.robots.server.util

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.filter.Filter
import ch.qos.logback.core.spi.FilterReply

/**
 * @author lars
 */
class LogFilter : Filter<ILoggingEvent>() {
    override fun decide(event: ILoggingEvent?): FilterReply = if (event == null) {
        FilterReply.NEUTRAL
    } else {
        if (event.loggerName.contains("javalin|jetty".toRegex())) {
            if (event.level.toInt() > Level.ERROR_INT) FilterReply.ACCEPT else FilterReply.DENY
        } else {
            FilterReply.ACCEPT
        }
    }
}