package de.westermann.robots.server.service

import de.westermann.robots.datamodel.DeviceManager
import de.westermann.robots.datamodel.IRobotClient
import de.westermann.robots.datamodel.Robot
import de.westermann.robots.datamodel.util.*
import de.westermann.robots.server.util.Configuration
import de.westermann.robots.server.util.WhoBlocks
import mu.KotlinLogging
import java.net.*
import java.nio.ByteBuffer
import java.nio.charset.Charset
import kotlin.jvm.Volatile

/**
 * @author lars
 */
object RobotUdpService : ThreadedService(false), Service1<Int> {

    override val logger = KotlinLogging.logger {}

    fun currentMillis() = System.currentTimeMillis()

    @Volatile
    private var server: DatagramSocket? = null

    private var robots: Map<SocketAddress, State> = emptyMap()
    private val disconnectTimeout = Configuration.Network.disconnectTimeout

    class State(
            val robot: Robot,
            var lastPing: Long = currentMillis()
    )

    override fun start() {
        start(Configuration.Network.robotUdpPort)
    }

    override fun start(arg: Int) {
        logger.info { "Start robot udp server on port $arg..." }

        try {
            server = DatagramSocket(arg).also {
                it.reuseAddress = true
                it.soTimeout = 100
            }
        } catch (_: BindException) {
            logger.error {
                "Cannot start robot udp server cause port $arg is already in use!" + (WhoBlocks.port(arg)?.let {
                    " (by '${it.name}': ${it.id})"
                } ?: "")
            }
            return
        }

        super.start()
    }

    private fun send(address: SocketAddress, buffer: ByteArray) {
        val packet = DatagramPacket(buffer, buffer.size, address)
        RobotUdpService.server?.send(packet)
    }

    private fun addRobot(address: SocketAddress) {
        if (robots.containsKey(address)) return
        val robot = Robot(DeviceManager.robots.nextId)
        robot.lineFollower = LineFollower(LineFollower.State.DISABLED, Color.BLACK, Color.WHITE)
        robot.kicker = Kicker(true)
        DeviceManager.robots += robot

        val iRobotClient = object : IRobotClient {
            override fun setForegroundColor() {
                ByteArray(0)
                        .putUnsignedByte(31)
                        .send(address)
            }

            override fun setBackgroundColor() {
                ByteArray(0)
                        .putUnsignedByte(32)
                        .send(address)
            }

            override fun resetMap() {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun pid(enable: Boolean) {
                ByteArray(0)
                        .putUnsignedByte(30)
                        .putUnsignedByte(if (enable) 1 else 0)
                        .send(address)
            }

            override fun speed(speed: Double) {
                ByteArray(0)
                        .putUnsignedByte(11)
                        .putFloat32(speed.toFloat())
                        .send(address)
            }

            override fun track(track: Track) {
                ByteArray(0)
                        .putUnsignedByte(10)
                        .putFloat32(track.x.toFloat())
                        .putFloat32(track.y.toFloat())
                        .send(address)
            }

            override fun trim(trim: Double) {
                ByteArray(0)
                        .putUnsignedByte(12)
                        .putFloat32(trim.toFloat())
                        .send(address)
            }

            override fun kick() {
                ByteArray(0)
                        .putUnsignedByte(20)
                        .send(address)
            }

            override fun setName(name: String) {
                ByteArray(0)
                        .putUnsignedByte(40)
                        .putString(name)
                        .send(address)
            }

            override fun setColor(color: Color) {
                ByteArray(0)
                        .putUnsignedByte(41)
                        .putString(color.name ?: "black")
                        .send(address)
            }

        }

        robot.nameProperty.onChange { newValue, _ ->
            iRobotClient.setName(newValue)
        }
        robot.colorProperty.onChange { newValue, _ ->
            iRobotClient.setColor(newValue)
        }
        robot.trackProperty.onChange { newValue, _ ->
            iRobotClient.track(newValue)
        }
        robot.speedProperty.onChange { newValue, _ ->
            iRobotClient.speed(newValue)
        }
        robot.trimProperty.onChange { newValue, _ ->
            iRobotClient.trim(newValue)
        }
        robot.lineFollowerPropety.onChange { newValue, oldValue ->
            if (newValue.state != oldValue.state) {
                when (newValue.state) {
                    LineFollower.State.RUNNING -> iRobotClient.pid(true)
                    LineFollower.State.DISABLED -> iRobotClient.pid(false)
                    LineFollower.State.UNAVAILABLE -> {
                        //NOTHING
                    }
                }
            }
        }
        robot.setForegroundColor.on {
            iRobotClient.setForegroundColor()
        }
        robot.setBackgroundColor.on {
            iRobotClient.setBackgroundColor()
        }
        robot.kick.on {
            iRobotClient.kick()
        }
        robot.pid.on {
            when {
                robot.lineFollower.state == LineFollower.State.RUNNING -> {
                    robot.lineFollower = robot.lineFollower.copy(state = LineFollower.State.DISABLED)
                    iRobotClient.pid(false)
                }
                robot.lineFollower.state == LineFollower.State.DISABLED -> {
                    robot.lineFollower = robot.lineFollower.copy(state = LineFollower.State.RUNNING)
                    iRobotClient.pid(true)
                }
                else -> {

                }
            }
        }

        robots += address to State(robot)
    }

    private fun removeRobot(address: SocketAddress) {
        val robot = robots[address]?.robot ?: return
        DeviceManager.robots -= robot
        robots -= address
    }

    private fun checkTimeouts() {
        val now = currentMillis()

        robots.filter { (_, state) ->
            now - state.lastPing > disconnectTimeout
        }.forEach { socketAddress, _ ->
            removeRobot(socketAddress)
        }
    }

    override fun run() {
        try {
            server?.let { server ->
                val packet = DatagramPacket(ByteArray(64), 64)
                server.receive(packet)

                if (!robots.containsKey(packet.socketAddress)) {
                    addRobot(packet.socketAddress)
                }

                //TODO
                robots[packet.socketAddress]?.let { state ->
                    state.lastPing = currentMillis()

                    //val messageVersion = packet.data[0].toPositiveInt()
                    val messageType = packet.data[1].toPositiveInt()
                    val buffer = packet.data.take(packet.length).drop(2).toByteArray()

                    when (messageType) {
                        0 -> {
                            ByteArray(0)
                                    .putUnsignedByte(0)
                                    .send(packet.socketAddress)
                        }
                        1 -> {
                            state.robot.iRobotServer?.version(Version.parse(buffer.toStr()))
                        }
                        2 -> {
                            state.robot.iRobotServer?.name(buffer.toStr())
                        }
                        3 -> {
                            state.robot.iRobotServer?.color(Color.parse(buffer.toStr()))
                        }
                        4 -> {
                            state.robot.iRobotServer?.availableColors(buffer.toStr().split(";").map { Color.parse(it) })
                        }
                        5 -> {
                            state.robot.iRobotServer?.currentColor(Color(buffer[0].toPositiveInt(), buffer[1].toPositiveInt(), buffer[2].toPositiveInt()))
                        }
                        6 -> {
                            state.robot.iRobotServer?.energy(Energy(buffer.toFloat().toDouble(), Energy.State.UNKNOWN))
                        }
                        else -> {
                            logger.warn { "Unsupported message: $messageType" }
                        }
                    }
                }

                checkTimeouts()
            }
        } catch (_: SocketTimeoutException) {
            checkTimeouts()
        } catch (e: SocketException) {
            if (server != null) {
                logger.error { "Socket error in robot udp server" }
            }
        }
    }

    override fun stop() {
        server?.let {
            server = null
            it.close()
        }
        super.stop()
    }


    private fun Int.toByteArray(): ByteArray =
            ByteBuffer.allocate(java.lang.Integer.BYTES)
                    .putInt(this)
                    .array()

    private fun Float.toByteArray(): ByteArray =
            ByteBuffer.allocate(java.lang.Integer.BYTES)
                    .putFloat(this)
                    .array()

    fun Byte.toPositiveInt() = toInt() and 0xFF

    private fun ByteArray.toInt(): Int = ByteBuffer.wrap(this).int
    private fun ByteArray.toFloat(): Float = ByteBuffer.wrap(this).float
    private fun ByteArray.toStr(): String = toString(Charset.defaultCharset())

    private fun ByteArray.putUnsignedByte(byte: Int): ByteArray {
        return this + byte.toByte()
    }

    private fun ByteArray.putFloat32(float: Float): ByteArray {
        return this + float.toByteArray()
    }

    private fun ByteArray.putString(str: String): ByteArray {
        return this + str.toByteArray()
    }

    private fun ByteArray.send(address: SocketAddress) {
        send(address, byteArrayOf(1) + this)
    }

}