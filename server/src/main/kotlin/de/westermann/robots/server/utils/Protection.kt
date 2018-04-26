package de.westermann.robots.server.utils

import com.lambdaworks.crypto.SCryptUtil
import mu.KotlinLogging
import java.nio.charset.Charset
import java.security.MessageDigest
import kotlin.experimental.and

/**
 * @author lars
 */
object Protection {
    private val logger = KotlinLogging.logger {}

    fun checkPassword(password: String?, type: Type, passphrase: String?): Boolean = try {
        when {
            type == Type.NONE -> true
            password == null || passphrase == null -> false
            type == Type.PLAIN -> password == passphrase
            type == Type.SHA_512 -> password.sha512() == passphrase
            type == Type.S_CRYPT -> SCryptUtil.check(password, passphrase)
            else -> false
        }
    } catch (e: Exception) {
        logger.error("Error while checking password against type '$type'", e)
        false
    }

    private val hexArray = "0123456789ABCDEF".toCharArray()
    private fun bytesToHex(bytes: ByteArray): String {
        val hexChars = CharArray(bytes.size * 2)
        for (j in bytes.indices) {
            val v = (bytes[j] and 0xFF.toByte()).toInt()

            hexChars[j * 2] = hexArray[v ushr 4]
            hexChars[j * 2 + 1] = hexArray[v and 0x0F]
        }
        return String(hexChars)
    }

    private fun String.sha512(): String = bytesToHex(MessageDigest.getInstance("SHA-512").digest(this.toByteArray(Charset.forName("UTF-8"))))

    enum class Type {
        NONE, PLAIN, SHA_512, S_CRYPT
    }
}