package de.westermann.robots.server.utils

import mu.KotlinLogging
import java.io.IOException
import java.net.URLClassLoader
import java.nio.file.Files
import java.nio.file.Paths
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.jar.Manifest
import kotlin.reflect.KProperty1
import kotlin.reflect.KVisibility
import kotlin.reflect.full.memberProperties

/**
 * @author lars
 */
object Environment {
    private val logger = KotlinLogging.logger {}
    private const val UNKNOWN = "Unknown"

    private val manifest: Manifest? = this::class.java.classLoader.let {
        it as? URLClassLoader
    }?.let {
        try {
            Manifest(it.findResource("META-INF/MANIFEST.MF").openStream())
        } catch (e: IOException) {
            logger.error { "Cannot read manifest" }
            null
        }
    }

    abstract class EnvInformation {
        internal val printer: Printer
            get() = Printer.Table("$name Information",
                    properties.map {
                        Printer.Line(
                                "$name-${"${it.name.capitalize()}:"}",
                                it.getter.call(this@EnvInformation).toString()
                        )
                    }
            )

        private val name: String
            get() = this::class.simpleName?.replace("[A-Za-z]*\\\$([A-Za-z]+)\\\$[0-9]*".toRegex(), "$1") ?: UNKNOWN

        private val properties: List<KProperty1<out EnvInformation, Any?>>
            get() = this::class.memberProperties.filter {
                it.visibility == KVisibility.PUBLIC
            }.toList()
    }

    object Build : EnvInformation() {
        val version: String =
                manifest?.mainAttributes?.getValue("Build-Version")
                        ?: Paths.get("build.gradle").let {
                            if (Files.exists(it)) {
                                Files.lines(it).filter {
                                    it.toLowerCase().startsWith("version")
                                }.findFirst().let {
                                    if (it.isPresent) {
                                        it.get().replace("version|'|\"|=".toRegex(), "").trim() + " Unpacked"
                                    } else {
                                        null
                                    }
                                }
                            } else {
                                null
                            }
                        }
                        ?: UNKNOWN

        val time: Date =
                manifest?.mainAttributes?.getValue("Build-Time")?.let {
                    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss z")
                    try {
                        format.parse(it)
                    } catch (e: ParseException) {
                        logger.error { "Cannot parse build time" }
                        null
                    }
                } ?: Date()

        val tools: String =
                manifest?.mainAttributes?.getValue("Build-Tools") ?: UNKNOWN

        val system: String =
                manifest?.mainAttributes?.getValue("Build-System") ?: UNKNOWN
    }

    object Execution : EnvInformation() {
        val java: String =
                "java-${System.getProperty("java.version")}"

        val system: String =
                "${System.getProperty("os.name")} '${System.getProperty("os.version")}' (${System.getProperty("os.arch")})"

        val language: String =
                System.getProperty("user.language")

        val windows: Boolean =
                system.toLowerCase().contains("windows")
    }

    val INDENT = " ".repeat(2)

    fun log(l: (String) -> Unit = {
        logger.info { it }
    }) = Printer.Table(
            "Environment information",
            listOf(Build, Execution).map {
                it.printer
            }
    ).log(l)
}