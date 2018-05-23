from math import pi

import devices
from util.track import Track

MAX_SPEED = 100

DIFF = pi / 4
DEG1 = pi / 2
DEG2 = pi
DEG3 = pi * 3 / 2

__speed = 0.8
__trim = 0.0

__min_left_speed: float = 0
__min_right_speed: float = 0

__left_speed: float = 0
__right_speed: float = 0


def update():
    devices.left_motor.duty_cycle_sp = ((max(__left_speed, __min_left_speed) * __speed + __trim) * MAX_SPEED)
    devices.right_motor.duty_cycle_sp = ((max(__right_speed, __min_right_speed) * __speed - __trim) * MAX_SPEED)


@property
def speed() -> float:
    return __speed


@speed.setter
def speed(value: float):
    global __speed
    __speed = value


def on_track(track: Track):
    global __left_speed, __right_speed, last_drive_mode
    angle = track.angle()
    radius = track.radius()

    if last_drive_mode != 0:
        last_drive_mode = 0
        devices.left_motor.run_direct()
        devices.right_motor.run_direct()

    if radius == 0:
        __left_speed = 0
        __right_speed = 0
    elif angle < DEG1:
        __left_speed = 1 * radius
        __right_speed = ((angle - DIFF) / DIFF) * radius
    elif angle < DEG2:
        __left_speed = -((angle - DEG1 - DIFF) / DIFF) * radius
        __right_speed = 1 * radius
    elif angle < DEG3:
        __left_speed = -1 * radius
        __right_speed = -((angle - DEG2 - DIFF) / DIFF) * radius
    else:
        __left_speed = ((angle - DEG3 - DIFF) / DIFF) * radius
        __right_speed = -1 * radius

    update()


last_drive_mode = -1


def direct(left: float, right: float):
    global last_drive_mode, __min_left_speed, __min_right_speed
    if last_drive_mode != 0:
        last_drive_mode = 0
        devices.left_motor.run_direct()
        devices.right_motor.run_direct()

    __min_left_speed = left
    __min_right_speed = right
    update()


def relative(s: float, left: float, right: float):
    devices.left_motor.stop()
    devices.right_motor.stop()

    devices.left_motor.speed_sp = ((s * __speed + __trim) * MAX_SPEED * 4)
    devices.right_motor.speed_sp = ((s * __speed - __trim) * MAX_SPEED * 4)

    devices.left_motor.position_sp = left
    devices.right_motor.position_sp = right

    global last_drive_mode
    last_drive_mode = 1
    devices.left_motor.run_to_rel_pos()
    devices.right_motor.run_to_rel_pos()

    devices.left_motor.wait_while("running", 5000)
    devices.right_motor.wait_while("running", 5000)


def stop():
    devices.left_motor.stop()
    devices.right_motor.stop()
