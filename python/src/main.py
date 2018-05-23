#!/usr/bin/env python3
import time
import uuid

import paho.mqtt.client as paho

import devices
import kicker
import mqtt
import pid_controller

client: paho.Client = None

PORT = 7500


def run():
    try:
        main()
    finally:
        print("Cleaning up")
        mqtt.stop(client)
        pid_controller.stop()
        print("Exit")


def main():
    global client
    client_id = str(uuid.uuid4())
    client = paho.Client(client_id=client_id, clean_session=True)

    # address, port = discover.find(PORT)
    #
    # mqtt.on_disconnect = reconnect
    # mqtt.connect(client, client_id, address, port)

    # input('Press Enter to start pid...\n')
    # pid_controller.start()

    while True:
        input("Press enter to kick...\n")
        kicker.kick()

    input('Press Enter to exit...\n')
    mqtt.on_disconnect = lambda: None


def print_color():
    while True:
        print(devices.current_color())
        time.sleep(1)


def reconnect():
    global client
    mqtt.stop(client)

    run()


if __name__ == '__main__':
    run()
