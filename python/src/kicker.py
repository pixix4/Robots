import devices
from time import sleep
first = True


def calibrate():
    devices.extra_motor.stop_action = "brake"
    devices.extra_motor.speed_sp = -100
    devices.extra_motor.run_timed(time_sp=2000)
    devices.extra_motor.wait_until("stalled", 2000)
    devices.extra_motor.stop()
    sleep(1)
    devices.extra_motor.position = 0



def kick():
    global first
    if first:
        first = False
        calibrate()
    devices.extra_motor.speed_sp = 800
    devices.extra_motor.position_sp = 150
    devices.extra_motor.run_to_abs_pos()
    devices.extra_motor.wait_while("running")
    devices.extra_motor.position_sp = 0
    devices.extra_motor.run_to_abs_pos()
