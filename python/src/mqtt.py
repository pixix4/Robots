import paho.mqtt.client as paho

import connection_client

__client: paho.Client = None
__client_id: str


def on_message(client, data, message):
    connection_client.parse(message.payload.decode('utf-8').split('|'))


def on_connect(client, data, flag, rc):
    print("Connect")


def on_disconnect():
    pass


def __on_disconnect(client, data, rc):
    print("Disconnect")
    on_disconnect()


def send(message):
    if __client is not None:
        __client.publish(__client_id, message)


def connect(client: paho.Client, client_id: str, address: str, port: int):
    print("Try to connect to server " + address + ":" + str(port))
    global __client, __client_id
    __client_id = client_id
    __client = client
    __client.on_message = on_message
    __client.on_connect = on_connect
    __client.on_disconnect = __on_disconnect

    __client.connect(address, port=port, keepalive=5)
    __client.loop_start()

    __client.subscribe(client_id, qos=0)


def stop():
    global __client
    if __client is not None:
        __client.loop_stop()
        __client.disconnect()

        __client = None
