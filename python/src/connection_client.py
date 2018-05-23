from typing import List

import drive
from util.color import Color
from util.track import Track


def foreground_color(color: Color):
    pass


def background_color(color: Color):
    pass


def reset_map():
    pass


def pid(enable: bool):
    pass


def speed(speed: float):
    pass


def track(track: Track):
    drive.on_track(track)


def trim(trim: float):
    pass


def parse(command: List[str]):
    if command[0] == "track":
        track(Track.parse(command[1]))
