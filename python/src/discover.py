import ipaddress
import socket
import time
from struct import pack, unpack
from typing import Tuple


def __int_to_data(number: int):
    return pack(">i", number)


def __data_to_int(data):
    return unpack(">i", data)[0]


def __get_ips():
    return [ip for ip in socket.gethostbyname_ex(socket.gethostname())[2] if
            not (ip.startswith("127.") or ip.startswith("169."))]


def __ip_to_nets(ip_str: str):
    return [str(ipaddress.ip_network(ip_str + "/" + suffix, False).broadcast_address) for suffix in ["24", "16"]]


def get_broadcasts():
    return [it for l in [__ip_to_nets(it) for it in __get_ips()] for it in l]


def find(port: int) -> Tuple[str, int]:
    sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    sock.setsockopt(socket.SOL_SOCKET, socket.SO_BROADCAST, 1)
    sock.settimeout(1)

    addresses = [(it, port) for it in get_broadcasts()]
    print("Try to find server in " + str(addresses))

    while True:
        addresses = [(it, port) for it in get_broadcasts()]
        for address in addresses:
            sock.sendto(__int_to_data(0), address)
            try:
                data, server = sock.recvfrom(4)
                return server[0], __data_to_int(data)
            except socket.timeout:
                time.sleep(1)
