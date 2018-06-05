package de.westermann.robots.server.util

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

/**
 * @author lars
 */
object WhoBlocks {

    private fun exec(command: String): List<String> {
        val process = Runtime.getRuntime().exec(command)
        val reader = BufferedReader(InputStreamReader(process.inputStream))
        val result = reader.readLines()
        process.destroy()
        return result
    }

    fun port(port: Int): Process? = if (Environment.Execution.windows) {
        try {
            exec("netstat -aon").find { it.contains(":$port") }?.let {
                "([0-9]+)\$".toRegex().find(it)?.groupValues?.let {
                    Process("process", it[1].toInt())
                }
            }
        } catch (_: IOException) {
            null
        }
    } else {
        var process = try {
            exec("ss -tulpn").find { it.contains(":$port") }?.let {
                "\\(\\(\"(.+)\",pid=([0-9]+)".toRegex().find(it)?.groupValues?.let {
                    Process(it[1], it[2].toInt())
                }
            }
        } catch (_: IOException) {
            null
        }
        if (process == null) {
            process = try {
                exec("netstat -tulpn").find { it.contains(":$port") }?.let {
                    "\\(\\(\"(.+)\",pid=([0-9]+)".toRegex().find(it)?.groupValues?.let {
                        Process(it[1], it[2].toInt())
                    }
                }
            } catch (_: IOException) {
                null
            }
        }
        process
    }

    data class Process(
            val name: String,
            val id: Int
    )
}