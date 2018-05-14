package de.westermann.robots.server.util

import com.vaadin.sass.internal.ScssContext
import com.vaadin.sass.internal.ScssStylesheet
import com.vaadin.sass.internal.handler.SCSSDocumentHandlerImpl
import com.vaadin.sass.internal.handler.SCSSErrorHandler
import mu.KotlinLogging
import java.nio.file.*
import kotlin.streams.toList


class ResourceHandler(
        private val classPath: String,
        private val destination: Path
) {
    private val logger = KotlinLogging.logger {}

    init {
        val uri = this::class.java.classLoader.getResource(classPath).toURI()
        var fileSystem: FileSystem? = null
        val src = if (uri.scheme == "jar") {
            fileSystem = FileSystems.newFileSystem(uri, mutableMapOf<String, Any?>())
            fileSystem.getPath(classPath)
        } else {
            Paths.get(uri)
        }

        Files.walk(src).forEach {
            val name = it.toAbsolutePath().toString().let {
                it.drop(classPath.length + it.indexOf(classPath, ignoreCase = true)).dropWhile {
                    it == '/' || it == '\\'
                }
            }
            if (Files.isDirectory(it)) {
                Files.createDirectories(destination.resolve(name))
            } else {
                Files.copy(it, destination.resolve(name))
            }
        }

        fileSystem?.close()

        compileSass()
    }

    fun walkFiles(walker: (path: String, file: Path) -> Unit) {
        Files.walk(destination).filter { Files.isRegularFile(it) }.map {
            it to it.toAbsolutePath().toString().let {
                it.drop(classPath.length + it.indexOf(classPath, ignoreCase = true)).dropWhile {
                    it == '/' || it == '\\'
                }
            }
        }.forEach {
            walker(it.second, it.first)
        }
    }

    fun find(name: String): Path? = Files.walk(destination).toList().find {
        it.fileName.toString() == name
    }

    fun compileSass() {
        val style = destination.resolve("stylesheets")

        Files.write(
                style.resolve("_color.scss"),
                ColorScheme.colorMap.map { (key, value) -> "$key: $value;" },
                StandardOpenOption.TRUNCATE_EXISTING
        )

        Files.find(style, 100, { file, _ ->
            Files.isRegularFile(file) && !file.fileName.toString().startsWith('_')
        }, emptyArray()).forEach { srcFile ->
            val destFile = srcFile.resolveSibling(srcFile.fileName.toString().replace("\\.s[ac]ss".toRegex(), ".css"))
            try {
                val scss = ScssStylesheet.get(
                        srcFile.toAbsolutePath().toString(),
                        null,
                        SCSSDocumentHandlerImpl(),
                        SCSSErrorHandler()
                )

                scss.compile(ScssContext.UrlMode.MIXED)
                val writer = Files.newBufferedWriter(destFile, StandardOpenOption.CREATE)
                scss.write(writer)
                writer.close()
            } catch (e: Exception) {
                logger.error("Cannot compile stylesheet '${srcFile.fileName}'!", e)
            }
        }


    }

    companion object {
        fun getMimeType(file: Path): String = when(file.fileName.toString().split(".").last().toLowerCase()) {
            "html", "htm" -> "text/html"
            "css" -> "text/css"
            "js" -> "text/javascript"
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "gif" -> "text/gif"
            else -> "text/plain"
        }
    }
}