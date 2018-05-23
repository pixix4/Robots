package de.westermann.robots.lego

import de.westermann.robots.datamodel.util.Random
import kotlin.concurrent.thread
import kotlin.system.measureTimeMillis

class Main {
    companion object {
        fun Double.format(digits: Int): String = java.lang.String.format("%.${digits}f", this)

        /**
         * This is the main entry point
         */
        @JvmStatic
        fun main(args: Array<String>) {
            Runtime.getRuntime().addShutdownHook(thread(start = false, name = "shutdown") {
                DiscoveryClient.stop()
                MqttClient.stop()

                Devices.leftMotor.stop()
                Devices.rightMotor.stop()
                Devices.extraMotor.stop()
            })

            Devices.init()

            test(1000)

            findAndConnect()

            while (true) {
            }
        }

        fun test(add: Int) {
            println("Insertion test for $add items")
            val repeat = 10
            val drop = 5


            println("Test immutable list (${repeat}x, drop first $drop)...")
            val t2 = mutableListOf<Long>()
            for (i in 0..repeat) {
                print(" Test $i...\r")
                t2.add(measureTimeMillis {
                    testMutable(add)
                })
            }
            println("-> ${t2.drop(drop).average().format(4)}ms | max: ${t2.drop(drop).max()}ms | min: ${t2.drop(drop).min()}ms")


            println("Test mutable list (${repeat}x, drop first $drop)...")
            val t1 = mutableListOf<Long>()
            for (i in 0..repeat) {
                print(" Test $i...\r")
                t1.add(measureTimeMillis {
                    testMutable(add)
                })
            }
            println("-> ${t1.drop(drop).average().format(4)}ms | max: ${t1.drop(drop).max()}ms | min: ${t1.drop(drop).min()}ms")



            println("Test immutable list (${repeat}x, drop first $drop)...")
            val t3 = mutableListOf<Long>()
            for (i in 0..repeat) {
                print(" Test $i...\r")
                t3.add(measureTimeMillis {
                    testMutable(add)
                })
            }
            println("-> ${t3.drop(drop).average().format(4)}ms | max: ${t3.drop(drop).max()}ms | min: ${t3.drop(drop).min()}ms")


            println("Test mutable list (${repeat}x, drop first $drop)...")
            val t4 = mutableListOf<Long>()
            for (i in 0..repeat) {
                print(" Test $i...\r")
                t4.add(measureTimeMillis {
                    testMutable(add)
                })
            }
            println("-> ${t4.drop(drop).average().format(4)}ms | max: ${t4.drop(drop).max()}ms | min: ${t4.drop(drop).min()}ms")

        }

        fun testMutable(add: Int) {
            val list = mutableListOf<Double>()
            for (i in 0..add) {
                list.add(Random.double())
            }
        }

        fun testImmutable(add: Int) {
            var list = emptyList<Double>()
            for (i in 0..add) {
                list += Random.double()
            }
        }

        const val port: Int = 7500

        fun findAndConnect() {
            DiscoveryClient.find(port) { address, port ->
                MqttClient.start(address, port) {
                    println("------ Connection lost ------")
                    findAndConnect()
                }
            }
        }
    }
}
