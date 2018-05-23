import paho.mqtt.client as paho

import connection_client


def on_message(client, data, message):
    connection_client.parse(message.payload.decode('utf-8').split('|'))


def on_connect(client, data, flag, rc):
    print("Connect")


def on_disconnect():
    pass


def __on_disconnect(client, data, rc):
    print("Disconnect")
    on_disconnect()


def connect(client: paho.Client, client_id: str, address: str, port: int):
    print("Try to connect to server " + address + ":" + str(port))
    client.on_message = on_message
    client.on_connect = on_connect
    client.on_disconnect = __on_disconnect

    client.connect(address, port=port, keepalive=5)
    client.loop_start()

    client.publish(client_id, "test")

    client.subscribe(client_id, qos=1)


def stop(client: paho.Client):
    client.loop_stop()
    client.disconnect()
