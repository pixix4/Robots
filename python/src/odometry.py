import math
import time

import devices
from util.coordinate import Coordinate


class Odometry:
    def __init__(self):
        self.conversion_value = 0.26466666  # mm per motor tick

        # Motor variables
        self.last_r = 0  # Last motorR position
        self.last_l = 0  # Last motorL position

        # Position variables
        self.pos_x = 0  # X-Position in mm
        self.pos_y = 0  # Y-Position in mm
        self.heading = 0  # Angle in degrees

        self.gyro_offset = 0

    def odo_update(self):
        now_r = devices.right_motor.position
        now_l = devices.right_motor.position

        ang = devices.gyro_sensor.angle + self.gyro_offset

        dif_r = now_r - self.last_r
        dif_l = now_l - self.last_l
        self.last_r = now_r
        self.last_l = now_l

        dist = (dif_r + dif_l) / 2 * self.conversion_value

        self.pos_x = self.pos_x + dist * math.cos(ang * 2 * math.pi / 360)
        self.pos_y = self.pos_y + dist * math.sin(ang * 2 * math.pi / 360)
        self.heading = ang % 360

    def current(self) -> Coordinate:
        return Coordinate(int(self.pos_x), int(self.pos_y), self.heading)

    def reset(self):
        self.last_r = 0
        self.last_l = 0

        self.pos_x = 0
        self.pos_y = 0
        self.heading = 0

    @staticmethod
    def calibrate_gyro():
        # Robot needs to stay completely still for that!!!
        devices.gyro_sensor.mode = 'GYRO-ANG'
        time.sleep(0.01)
        devices.gyro_sensor.mode = 'GYRO-CAL'
        time.sleep(0.01)
        devices.gyro_sensor.mode = 'GYRO-ANG'
        # Let the calibration values settle
        time.sleep(0.2)


odometry = Odometry()
