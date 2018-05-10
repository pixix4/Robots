package de.westermann.robots.server.utils

import de.westermann.robots.datamodel.util.Color
import mu.KotlinLogging
import org.apache.commons.io.FileUtils
import java.io.IOException
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Proxy
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.reflect.KType
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaMethod
import kotlin.reflect.jvm.jvmErasure

/**
 * @author lars
 */
object Configuration {
    private val logger = KotlinLogging.logger {}
    private const val CONF_NAME = "robots.conf"

    interface Properties {
        @Description("Port for the web interface")
        val webPort: Int
            get() = 8080

        @Description("Port for robot connections")
        val robotPort: Int
            get() = 7510

        @Description("Port for udp discovery messages")
        val discoveryPort: Int
            get() = 7500

        @Description("Set the protection type for controller connections")
        val protection: Protection.Type
            get() = Protection.Type.NONE

        @Description("Sets the passphrase according to the protection type")
        val passphrase: String?
            get() = throw UnsetPropertyException("No password provided")

        @Description("Sets the default color scheme")
        val colorScheme: ColorScheme.Defaults
            get() = ColorScheme.Defaults.DEFAULT

        @Description("Sets the directory for temporary data")
        val tmpDirectory: Path
            get() = Paths.get("tmp")

        val primaryColor: Color?
            get() = null
        val primaryColorText: ColorScheme.Brightness?
            get() = null

        val primaryColorDark: Color?
            get() = null
        val primaryColorDarkText: ColorScheme.Brightness?
            get() = null

        val primaryColorLight: Color?
            get() = null
        val primaryColorLightText: ColorScheme.Brightness?
            get() = null

        val backgroundColorPrimary: Color?
            get() = null
        val backgroundColorSecondary: Color?
            get() = null
        val textColor: ColorScheme.Brightness?
            get() = null
    }

    annotation class Description(
            val text: String
    )

    class UnsetPropertyException(message: String) : Exception(message)

    fun log(l: (String) -> Unit = {
        logger.info { it }
    }) = Printer.Table(
            "Current configuration",
            propertyMap.map { (name, property) ->
                Printer.Line(
                        name.toProperty() + ":",
                        property.value?.let { "'$it'" } ?: "",
                        if (property.default) "(Default)" else ""
                )
            }
    ).log(l)

    fun help(l: (String) -> Unit = {
        logger.info { it }
    }) = Printer.Table(
            "Configuration help",
            Properties::class.memberProperties.map { prop ->
                Printer.Line(
                        prop.name.toProperty() + ":",
                        try {
                            prop.getter.call(emptyProperties)?.let { "'$it'" } ?: ""
                        } catch (_: InvocationTargetException) {
                            ""
                        },
                        "-${Environment.INDENT}" + (prop.findAnnotation<Description>()?.text ?: "").let {
                            ("$it (" + (if (prop.returnType.jvmErasure.java.isEnum) {
                                prop.returnType.jvmErasure.java.enumConstants.joinToString(", ") {
                                    "'$it'"
                                }
                            } else {
                                prop.returnType.jvmErasure.simpleName
                            }) + ")").trim()
                        }
                )
            }
    ).log(l)

    private fun String.toProperty() = replace("^([Gg])et".toRegex(), "").toUpperDashCase()

    private fun String.toMethod() = "get-$this".toCamelCase()

    fun load(args: List<String>) {
        findFiles().forEach(this::loadFile)
        loadArguments(args)
    }

    private fun findFiles(): List<Path> {
        val list = mutableListOf<Path>(Paths.get(CONF_NAME).toAbsolutePath())
        while (list.last().root != list.last().parent) {
            list.add(list.last().parent.parent.resolve(CONF_NAME))
        }
        return list.filter {
            Files.exists(it)
        }
    }


    private fun loadFile(file: Path) =
            loadMap(Files.readAllLines(file).map {
                it.replace("#.*".toRegex(), "").trim()
            }.filter(String::isNotEmpty).map {
                it.split(':', limit = 2).let {
                    it[0].trim() to if (it.size <= 1) {
                        null
                    } else {
                        it[1].trim()
                    }
                }
            }.groupBy { it.first }.mapValues {
                it.value.last().second
            })

    private fun loadArguments(args: List<String>) =
            loadMap(args.windowed(2, 1).filter { it.first().startsWith("--") }.map {
                if (it[1].startsWith("--")) {
                    it[0] to null
                } else {
                    it[0] to it[1]
                }
            }.groupBy { it.first }.mapValues {
                it.value.last().second
            }.mapKeys {
                it.key.substring(2)
            })

    private fun loadMap(map: Map<String, String?>) {
        map.mapKeys { it.key.toMethod() }.forEach { key, value ->
            propertyMap[key]?.let {
                propertyMap[key] = it.setValue(value)
            } ?: logger.warn { "Unknown property '${key.toProperty()}'" }
        }
    }

    private data class Property(
            val value: Any?,
            val type: KType,
            val default: Boolean = true
    ) {
        init {
            if (!valid()) {
                throw ClassCastException("Type ${
                if (value == null) "'NULL'" else value::class.simpleName
                } cannot be cast to ${type.jvmErasure.simpleName}${
                if (type.isMarkedNullable) "?" else ""
                }")
            }
        }

        fun setValue(value: String?): Property =
                try {
                    if (value == null) {
                        setValidValue(null)
                    } else {
                        when (type.jvmErasure) {
                            Int::class -> value.toIntOrNull()
                            Long::class -> value.toLongOrNull()
                            Double::class -> value.toDoubleOrNull()
                            Float::class -> value.toFloatOrNull()
                            Boolean::class -> value.toBoolean()
                            Path::class -> Paths.get(value)
                            String::class -> value
                            Color::class -> Color.parse(value)
                            else -> when {
                                type.jvmErasure.java.isEnum -> type.jvmErasure.java.enumConstants.find {
                                    it.toString().equals(value.replace("-", "_"), true)
                                }
                                type.jvmErasure.isSubclassOf(Path::class) -> Paths.get(value)
                                else -> null
                            }
                        }?.let {
                            setValidValue(it)
                        } ?: run {
                            logger.warn { "ClassCastException" }
                            this
                        }
                    }
                } catch (e: Exception) {
                    logger.warn { "An error occurred" }
                    this
                }


        private fun setValidValue(value: Any?): Property =
                if (valid(value)) {
                    copy(value = value, default = false)
                } else {
                    logger.warn { "Incompatible types" }
                    this
                }

        fun valid(x: Any? = value): Boolean = if (x == null) {
            type.isMarkedNullable
        } else {
            x::class == type.jvmErasure || x::class.isSubclassOf(type.jvmErasure)
        }
    }

    private val propertyMap: MutableMap<String, Property> = mutableMapOf()

    private val emptyProperties = object : Properties {}

    val properties: Properties = Proxy.newProxyInstance(
            this::class.java.classLoader,
            arrayOf(Properties::class.java)
    ) { _, method, _ ->
        val name = method.name
        if (propertyMap.containsKey(name)) {
            propertyMap[name]?.value
        } else {
            throw IllegalArgumentException("Cannot find property '$name'")
        }
    } as Properties

    fun tmp(subFolder: String? = null): Path =
            (subFolder?.let {
                properties.tmpDirectory.resolve(subFolder)
            } ?: properties.tmpDirectory)


    fun tmpClear(subFolder: String? = null) {
        try {
            FileUtils.deleteDirectory(subFolder?.let {
                properties.tmpDirectory.resolve(subFolder).toFile()
            } ?: properties.tmpDirectory.toFile())
        } catch (_:IOException) {

        }
    }

    init {
        Properties::class.memberProperties.forEach {
            try {
                propertyMap[it.getter.javaMethod?.name
                        ?: it.name] = Property(it.getter.call(emptyProperties), it.returnType)
            } catch (_: InvocationTargetException) {
                propertyMap[it.getter.javaMethod?.name ?: it.name] = Property(null, it.returnType)
            }
        }
    }
}