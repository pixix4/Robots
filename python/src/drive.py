from math import pi

import devices
from util.track import Track

MAX_SPEED = 100
speed = 0.8
trim = 0.0

DIFF = pi / 4
DEG1 = pi / 2
DEG2 = pi
DEG3 = pi * 3 / 2


def on_track(track: Track):
    angle = track.angle()
    radius = track.radius()

    if radius == 0:
        direct(0, 0)
    elif angle < DEG1:
        direct(1 * radius, ((angle - DIFF) / DIFF) * radius)
    elif angle < DEG2:
        direct(-((angle - DEG1 - DIFF) / DIFF) * radius, 1 * radius)
    elif angle < DEG3:
        direct(-1 * radius, -((angle - DEG2 - DIFF) / DIFF) * radius)
    else:
        direct(((angle - DEG3 - DIFF) / DIFF) * radius, -1 * radius)


last_drive_mode = -1


def direct(left: float, right: float):
    global last_drive_mode
    if last_drive_mode != 0:
        last_drive_mode = 0
        devices.left_motor.run_direct()
        devices.right_motor.run_direct()

    devices.left_motor.duty_cycle_sp = ((left * speed + trim) * MAX_SPEED)
    devices.right_motor.duty_cycle_sp = ((right * speed - trim) * MAX_SPEED)


def relative(s: float, left: float, right: float):
    devices.left_motor.stop()
    devices.right_motor.stop()

    devices.left_motor.speed_sp = ((s * speed + trim) * MAX_SPEED * 4)
    devices.right_motor.speed_sp = ((s * speed - trim) * MAX_SPEED * 4)

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
