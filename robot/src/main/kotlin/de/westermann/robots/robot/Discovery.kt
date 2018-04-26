package de.westermann.robots.robot

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.SocketAddress
import java.nio.charset.Charset
import kotlin.reflect.KClass

/**
 * @author lars
 */
sealed class Discovery(
        val address: SocketAddress?
) {

    private val type: Byte
        get() = this::class.java.isAnnotationPresent(MessageType::class.java).let {
            if (it) {
                this::class.java.getAnnotation(MessageType::class.java).type
            } else {
                0
            }
        }

    private val isRecieved: Boolean
        get() = address != null

    protected abstract fun encode(): ByteArray

    @MessageType(1)
    class Inquire private constructor(
            address: SocketAddress?,
            val designation: String,
            val identifier: String
    ) : Discovery(address) {

        constructor(designation: String, identifier: String) : this(null, designation, identifier)

        override fun encode(): ByteArray =
                (designation + SEPERATOR + identifier).toByteArray(Charset.forName("UTF-8"))


        companion object {
            private const val SEPERATOR = "\u0000\u0000"
            fun decode(address: SocketAddress, data: ByteArray): Inquire {
                val arr = data.toString(Charset.forName("UTF-8")).split(SEPERATOR)
                return Inquire(address, arr[0], arr[1])
            }
        }
    }

    @MessageType(2)
    class Response private constructor(
            address: SocketAddress?,
            val code: Int
    ) : Discovery(address) {

        constructor(code: Int) : this(null, code)

        override fun encode(): ByteArray = code.toByteArray()

        companion object {
            fun decode(address: SocketAddress, data: ByteArray): Response =
                    Response(
                            address,
                            data.toDataInt()
                    )
        }
    }

    @MessageType(3)
    class Accept private constructor(
            address: SocketAddress?,
            val code: Int
    ) : Discovery(address) {

        constructor(code: Int) : this(null, code)

        override fun encode(): ByteArray = code.toByteArray()

        companion object {
            fun decode(address: SocketAddress, data: ByteArray): Accept =
                    Accept(
                            address,
                            data.toDataInt()
                    )
        }
    }

    @MessageType(4)
    class Reject private constructor(
            address: SocketAddress?,
            val reason: Int
    ) : Discovery(address) {

        constructor(reason: Int) : this(null, reason)

        override fun encode(): ByteArray = reason.toByteArray()

        companion object {
            fun decode(address: SocketAddress, data: ByteArray): Reject =
                    Reject(
                            address,
                            data.toDataInt()
                    )
        }
    }

    annotation class MessageType(
            val type: Byte
    )

    companion object {

        private fun type(clazz: KClass<out Discovery>): Byte =
                clazz.java.isAnnotationPresent(MessageType::class.java).let {
                    if (it) {
                        this::class.java.getAnnotation(MessageType::class.java).type
                    } else {
                        0
                    }
                }

        fun receive(socket: DatagramSocket): Discovery {
            val header = DatagramPacket(ByteArray(HEADER_SIZE), HEADER_SIZE)
            socket.receive(header)

            val type = header.data[0]
            val length = header.data[1].toInt()

            val data = DatagramPacket(ByteArray(length), length)
            socket.receive(data)

            return when (type) {
                type(Inquire::class) -> Inquire.decode(data.socketAddress, data.data)
                type(Response::class) -> Response.decode(data.socketAddress, data.data)
                type(Accept::class) -> Accept.decode(data.socketAddress, data.data)
                type(Reject::class) -> Reject.decode(data.socketAddress, data.data)
                else -> throw IllegalArgumentException()
            }
        }

        fun send(data: Discovery, socket: DatagramSocket, target: SocketAddress) {
            val rawMessage: ByteArray = data.encode()
            val message = byteArrayOf(data.type, rawMessage.size.toByte()) + rawMessage
            socket.send(DatagramPacket(message, message.size, target))
        }

        private const val HEADER_SIZE = 2


        fun Int.toByteArray(): ByteArray {
            val arr = ByteArray(4)
            for (i in 0..3) {
                arr[i] = (this.shr(i * 8).and(0xFF)).toByte()
            }
            return arr
        }

        fun ByteArray.toDataInt(): Int {
            var int = 0
            for (i in 0..3) {
                int += this[i].toInt().shl(i * 8)
            }
            return int
        }
    }
}