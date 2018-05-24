from typing import List

import drive
import kicker
import pid_controller
from util.color import Color
from util.track import Track


def foreground_color(color: Color):
    pass


def background_color(color: Color):
    pass


def reset_map():
    pass


def pid(enable: bool):
    if enable:
        pid_controller.start()
    else:
        pid_controller.stop()


def speed(s: float):
    print("new speed {}".format(s))
    drive.speed(s)


def track(t: Track):
    drive.on_track(t)


def trim(t: float):
    drive.trim(t)


def kick():
    kicker.kick()


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
    else:
        print("Unknown command " + str(command))
