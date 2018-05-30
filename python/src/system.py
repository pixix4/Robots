import os.path
from typing import List, Tuple

from ev3dev.ev3 import Leds, PowerSupply

import util.color as color_const
from util.color import Color
from util.energy import Energy, EnergyState
from util.version import Version

__color_list = [color_const.CONST_RED, color_const.CONST_GREEN, color_const.CONST_AMBER, color_const.CONST_ORANGE,
                color_const.CONST_YELLOW]

__current_color: Color = color_const.CONST_TRANSPARENT
__current_name: str = ""
__current_version: Version = Version.parse("0.5.0-alpha")

__power = PowerSupply()

__active = False

__file_name: str = "robot.conf"


def __update_file():
    lines = ["name: " + __current_name, "color: " + str(__current_color)]

    lines = "\n".join(lines)
    with open(__file_name, 'w') as f:
        f.write(lines)
    f.close()


def load_file():
    if not os.path.isfile(__file_name): return

    global __current_name, __current_color
    lines: List[str] = []
    with open(__file_name, 'r') as f:
        lines = f.read().strip().split("\n")
    f.close()

    for line in lines:
        try:
            line = line.strip()
            if len(line) == 0:
                continue

            split = [it.strip() for it in line.split(":", 1)]

            if split[0] == "name":
                __current_name = split[1]
            elif split[0] == "color":
                __current_color = Color.parse(split[1])
        except:
            pass


def __update_led():
    c: Tuple[float, float, None]

    if __current_color == color_const.CONST_RED:
        c = Leds.RED
    elif __current_color == color_const.CONST_GREEN:
        c = Leds.GREEN
    elif __current_color == color_const.CONST_AMBER:
        c = Leds.AMBER
    elif __current_color == color_const.CONST_ORANGE:
        c = Leds.ORANGE
    elif __current_color == color_const.CONST_YELLOW:
        c = Leds.YELLOW
    else:
        c = Leds.BLACK

    Leds.set_color(Leds.LEFT, c, 1)
    Leds.set_color(Leds.RIGHT, c, 1)


def available_color() -> List[Color]:
    return __color_list


def color() -> Color:
    return __current_color


def set_color(value: Color):
    global __current_color
    if value not in __color_list:
        __current_color = color_const.CONST_TRANSPARENT
        Leds.all_off()
    else:
        __current_color = value
        __update_led()

    __update_file()


def active() -> bool:
    return __active


def set_active(value: bool):
    global __active
    if value == __active: return
    __active = value

    if value:
        __update_led()
    else:
        Leds.all_off()


def name() -> str:
    return __current_name


def set_name(value: str):
    global __current_name
    __current_name = value

    __update_file()


def version() -> Version:
    return __current_version


def energy() -> Energy:
    p = __power.measured_voltage
    if p > __power.max_voltage:
        p = __power.max_voltage
    elif p < __power.min_voltage:
        p = __power.min_voltage
    value = (p - __power.min_voltage) / (__power.max_voltage - __power.min_voltage)
    return Energy(round(value, 3), EnergyState.DISCHARGING)
