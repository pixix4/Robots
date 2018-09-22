package de.westermann.robots.server.service

/**
 * @author lars
 */
object ServiceManager {
    var services: List<Service> = emptyList()

    fun start(service: Service) {
        service.start()
        services += service
    }

    fun <T> start(service: Service1<T>, arg: T) {
        service.start(arg)
        services += service
    }

    fun stop(service: Service) {
        if (service !in services) {
            throw IllegalStateException()
        }

        service.stop()
        services -= service
    }

    fun stopAll() {
        services.forEach { service ->
            service.stop()
        }
        services = emptyList()
    }
}