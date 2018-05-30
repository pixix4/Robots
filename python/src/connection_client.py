from typing import List

import drive
import kicker
import odometry
import pid_controller
import system
from util.color import Color
from util.track import Track


def foreground_color():
    print("set foreground")
    pid_controller.foreground()


def background_color():
    print("set background")
    pid_controller.background()


def reset_map():
    print("reset map")
    odometry.odometry.calibrate_gyro()
    odometry.odometry.reset()


def pid(enable: bool):
    print("pid {}".format(enable))
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
        pid(command[1] == "true")
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
