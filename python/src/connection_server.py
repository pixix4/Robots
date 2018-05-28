from typing import List

import mqtt
from util.color import Color
from util.coordinate import Coordinate
from util.energy import Energy
from util.version import Version


def map(points: List[Coordinate]):
    pass


def current_color(color: Color):
    mqtt.send("currentColor|" + str(color))


def foreground_color(color: Color):
    mqtt.send("foregroundColor|" + str(color))


def background_color(color: Color):
    mqtt.send("backgroundColor|" + str(color))


def energy(energy: Energy):
    mqtt.send("energy|" + str(energy))


def version(version: Version):
    mqtt.send("version|" + str(version))


def name(name: str):
    mqtt.send("name|" + str(name))


def color(color: Color):
    mqtt.send("color|" + str(color))


def available_colors(colors: List[Color]):
    mqtt.send("availableColors|" + ";".join([str(it) for it in colors]))
