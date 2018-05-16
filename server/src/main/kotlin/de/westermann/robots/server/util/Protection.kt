package de.westermann.robots.server.util

import com.lambdaworks.crypto.SCryptUtil
import mu.KotlinLogging

/**
 * @author lars
 */
object Protection {
    private val logger = KotlinLogging.logger {}

    val loginRequired: Boolean
        get() = Configuration.properties.protection == Type.NONE

    fun login(password: String): Boolean = checkPassword(
            password,
            Configuration.properties.protection,
            Configuration.properties.passphrase
    )

    private fun checkPassword(password: String?, type: Type, passphrase: String?): Boolean = try {
        when {
            type == Type.NONE -> true
            password == null || passphrase == null -> false
            type == Type.PLAIN -> password == passphrase
            type == Type.S_CRYPT -> SCryptUtil.check(password, passphrase)
            else -> false
        }
    } catch (e: Exception) {
        logger.error("Error while checking password against type '$type'", e)
        false
    }

    enum class Type {
        NONE, PLAIN, S_CRYPT
    }
}