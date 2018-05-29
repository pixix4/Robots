#!/usr/bin/env python3
import threading
import time
import uuid

import paho.mqtt.client as paho

import connection_server
import devices
import discover
import mqtt
import pid_controller
import system
from odometry import odometry

client: paho.Client = None

__status_kill: threading.Event = None
__status_thread: threading.Thread = None

PORT = 7500


def run():
    system.load_file()
    try:
        main()
    finally:
        print("Cleaning up")
        mqtt.stop()
        pid_controller.stop()
        if not __status_kill is None:
            __status_kill.set()
            __status_thread.join()
        print("Exit")


def main():
    global client, __status_kill, __status_thread
    client_id = str(uuid.uuid4())
    client = paho.Client(client_id=client_id, clean_session=True)

    address, port = discover.find(PORT)

    mqtt.on_disconnect = reconnect
    mqtt.connect(client, client_id, address, port)

    odometry.calibrate_gyro()
    odometry.reset()

    connection_server.version(system.version())
    connection_server.name(system.name())
    connection_server.color(system.color())
    connection_server.available_colors(system.available_color())

    __status_kill = threading.Event()
    __status_thread = threading.Thread(target=send_status_thread, args=(__status_kill,))
    __status_thread.start()

    input('Press Enter to exit...\n')
    mqtt.on_disconnect = lambda: None


def send_status_thread(kill_event):
    while not kill_event.wait(1):
        odometry.odo_update()
        connection_server.current_position(odometry.current())
        connection_server.current_color(devices.current_color_as_obj())
        connection_server.energy(system.energy())


def print_color():
    while True:
        print(devices.current_color())
        time.sleep(1)


def reconnect():
    mqtt.stop()
    __status_kill.set()
    __status_thread.join()

    run()


if __name__ == '__main__':
    run()
