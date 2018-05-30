from typing import Tuple

import ev3dev.ev3 as ev3

from util.color import Color

left_motor: ev3.LargeMotor = ev3.LargeMotor("outB")
right_motor: ev3.LargeMotor = ev3.LargeMotor("outA")
extra_motor: ev3.MediumMotor = ev3.MediumMotor("outC")

gyro_sensor: ev3.GyroSensor = ev3.GyroSensor()
color_sensor: ev3.ColorSensor = ev3.ColorSensor()

color_sensor.mode = ev3.ColorSensor.MODE_RGB_RAW

COLOR_FACTOR = 1

__last_color = (0, 0, 0)


def current_color() -> Tuple[int, int, int]:
    global __last_color
    try:
        __last_color = min(int(color_sensor.red * COLOR_FACTOR), 255), \
                       min(int(color_sensor.green * COLOR_FACTOR), 255), \
                       min(int(color_sensor.blue * COLOR_FACTOR), 255)
    except ValueError:
        pass
    return __last_color


def current_color_as_obj() -> Color:
    h = current_color()
    return Color(h[0], h[1], h[2])
