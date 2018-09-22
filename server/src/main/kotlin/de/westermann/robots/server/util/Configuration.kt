package de.westermann.robots.server.util

import com.uchuhimo.konf.Config
import com.uchuhimo.konf.ConfigSpec
import com.uchuhimo.konf.OptionalItem
import com.uchuhimo.konf.source.LoadException
import com.uchuhimo.konf.source.base.toFlatMap
import de.westermann.robots.datamodel.util.*
import mu.KotlinLogging
import org.apache.commons.io.FileUtils
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.reflect.KProperty
import kotlin.streams.toList

/**
 * @author lars
 */
@Suppress("Unused", "PropertyName")
object Configuration {
    private val logger = KotlinLogging.logger {}

    private var config = Config {
        addSpec(GeneralSpec)
        addSpec(NetworkSpec)
        addSpec(SecuritySpec)
        addSpec(StyleSpec)
    }

    private class OptionalDelegate<T>(
            private val item: OptionalItem<T>
    ) {
        operator fun getValue(container: Any, property: KProperty<*>): T {
            return config[item]
        }

        operator fun setValue(container: Any, property: KProperty<*>, value: T) {
            config[item] = value
        }
    }

    private fun <T> c(item: OptionalItem<T>) = OptionalDelegate(item)

    object General {
        val demo by c(GeneralSpec.demo)
        val maxPhysicalControllerCount by c(GeneralSpec.maxPhysicalControllerCount)
        private val tmpDirectoryStr by c(GeneralSpec.tmpDirectory)
        val tmpDirectory: Path
            get() = Paths.get(tmpDirectoryStr)
    }

    private object GeneralSpec : ConfigSpec("general") {
        val demo by optional(false)
        val maxPhysicalControllerCount by optional(4)
        val tmpDirectory by optional("tmp")
    }

    object Network {
        val webPort by c(NetworkSpec.webPort)
        val robotUdpPort by c(NetworkSpec.robotUdpPort)
        val discoveryPort by c(NetworkSpec.discoveryPort)
        val disconnectTimeout by c(NetworkSpec.disconnectTimeout)
    }

    private object NetworkSpec : ConfigSpec("network") {
        val webPort by optional(8080)
        val robotUdpPort by optional(7520)
        val discoveryPort by optional(7500)
        val disconnectTimeout by optional(5000)
    }

    object Security {
        val protection by c(SecuritySpec.protection)
        val passphrase by c(SecuritySpec.passphrase)
        val controllerCodeLength by c(SecuritySpec.controllerCodeLength)
        val controllerCodeCharset by c(SecuritySpec.controllerCodeCharset)
    }

    private object SecuritySpec : ConfigSpec("security") {
        val protection by optional(Protection.Type.NONE)
        val passphrase by optional<String?>(null)
        val controllerCodeLength by optional(4)
        val controllerCodeCharset by optional("0-9")
    }

    object Style {
        val colorScheme by c(StyleSpec.colorScheme)
        val primaryColor by c(StyleSpec.primaryColor)
        val primaryColorText by c(StyleSpec.primaryColorText)
        val primaryColorDark by c(StyleSpec.primaryColorDark)
        val primaryColorDarkText by c(StyleSpec.primaryColorDarkText)
        val primaryColorLight by c(StyleSpec.primaryColorLight)
        val primaryColorLightText by c(StyleSpec.primaryColorLightText)
        val backgroundColorPrimary by c(StyleSpec.backgroundColorPrimary)
        val backgroundColorSecondary by c(StyleSpec.backgroundColorSecondary)
        val textColor by c(StyleSpec.textColor)
    }

    private object StyleSpec : ConfigSpec("style") {
        val colorScheme by optional(ColorDefaults.DEFAULT)
        val primaryColor by optional<String?>(null)
        val primaryColorText by optional<Brightness?>(null)
        val primaryColorDark by optional<String?>(null)
        val primaryColorDarkText by optional<Brightness?>(null)
        val primaryColorLight by optional<String?>(null)
        val primaryColorLightText by optional<Brightness?>(null)
        val backgroundColorPrimary by optional<String?>(null)
        val backgroundColorSecondary by optional<String?>(null)
        val textColor by optional<Brightness?>(null)
    }

    val ACCEPTED_TYPES = mapOf(
            setOf("conf") to "HOCON",
            setOf("json") to "JSON",
            setOf("properties") to "Properties",
            setOf("toml") to "TOML",
            setOf("xml") to "XML",
            setOf("yml", "yaml") to "YAML"
    )

    private const val CONF_NAME = "robots.conf"

    private val Path.parentDirs: List<Path>
        get() = toAbsolutePath().let {
            (it.parent?.parentDirs ?: emptyList()).plus(element = it)
        }

    private fun findFiles() =
            Paths.get("").parentDirs.map {
                it.resolve(CONF_NAME)
            }.filter {
                Files.exists(it)
            }

    fun tmp(subFolder: String? = null): Path =
            (subFolder?.let {
                Configuration.General.tmpDirectory.resolve(subFolder)
            } ?: Configuration.General.tmpDirectory)

    fun tmpClear(subFolder: String? = null) {
        try {
            FileUtils.deleteDirectory(subFolder?.let {
                Configuration.General.tmpDirectory.resolve(subFolder).toFile()
            } ?: Configuration.General.tmpDirectory.toFile())
        } catch (_: IOException) {

        }
    }

    fun load() {
        config = config.from.env()
        config = config.from.systemProperties()

        findFiles().forEach {
            import(it)
        }

        logger.info { config.layer.toFlatMap() }
    }


    fun import(path: Path = Paths.get("")) {
        if (Files.exists(path) && Files.isReadable(path)) {
            if (Files.isDirectory(path)) {
                val files: List<Path> = Files.list(path).toList().filter {
                    !Files.isDirectory(it) && Files.isReadable(it)
                }

                val configs = files.filter {
                    val ending = it.fileName.toString().split(".").last().toLowerCase()
                    ending in ACCEPTED_TYPES.keys.flatten()
                }

                configs.forEach {
                    import(it)
                }
            } else {
                val ending = path.fileName.toString().split(".").last().toLowerCase()
                if (ending in ACCEPTED_TYPES.keys.flatten()) {
                    try {
                        config = config.from.file(path.toFile())

                        logger.info { "Import '${path.toAbsolutePath()}'." }
                    } catch (_: LoadException) {
                        logger.warn { "Cannot load config file '${path.toAbsolutePath()}'!" }
                    }
                }
            }
        }
    }
}