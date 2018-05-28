from typing import List

import drive
import kicker
import pid_controller
import system
from util.color import Color
from util.track import Track


def foreground_color():
    pid_controller.foreground()


def background_color():
    pid_controller.background()


def reset_map():
    pass


def pid(enable: bool):
    if enable:
        pid_controller.start()
    else:
        pid_controller.stop()


def speed(s: float):
    drive.speed(s)


def track(t: Track):
    drive.on_track(t)


def trim(t: float):
    drive.trim(t)


def kick():
    kicker.kick()


def set_name(name: str):
    system.set_name(name)


def set_color(color: Color):
    system.set_color(color)


def parse(command: List[str]):
    if command[0] == "track":
        track(Track.parse(command[1]))
    elif command[0] == "kick":
        kick()
    elif command[0] == "speed":
        speed(float(command[1]))
    elif command[0] == "trim":
        trim(float(command[1]))
    elif command[0] == "pid":
        pid(bool(command[1]))
    elif command[0] == "setForegroundColor":
        foreground_color()
    elif command[0] == "setBackgroundColor":
        background_color()
    elif command[0] == "resetMap":
        reset_map()
    elif command[0] == "setName":
        set_name(command[1])
    elif command[0] == "setColor":
        set_color(Color.parse(command[1]))
