import threading
from time import sleep

import devices

__kicker_thread: threading.Thread = None
__kicker_kill: threading.Event = None
__kicker_fire: threading.Event = None


def __calibrate():
    devices.extra_motor.stop_action = "brake"
    devices.extra_motor.speed_sp = -100
    devices.extra_motor.run_timed(time_sp=2000)
    devices.extra_motor.wait_until("stalled", 2000)
    devices.extra_motor.stop()
    sleep(1)
    devices.extra_motor.position = 0


def __kick(kill, fire):
    while not kill.wait(0):
        if fire.wait(0):
            fire.clear()
            devices.extra_motor.speed_sp = 850
            devices.extra_motor.position_sp = 150
            devices.extra_motor.run_to_abs_pos()
            devices.extra_motor.wait_while("running")
            devices.extra_motor.position_sp = 0
            devices.extra_motor.run_to_abs_pos()
        else:
            sleep(0.1)


def kick():
    __kicker_fire.set()


def start():
    stop()
    global __kicker_kill, __kicker_thread, __kicker_fire
    __calibrate()

    __kicker_kill = threading.Event()
    __kicker_fire = threading.Event()

    __kicker_thread = threading.Thread(target=__kick, args=(__kicker_kill, __kicker_fire,))
    __kicker_thread.daemon = True
    __kicker_thread.start()


def stop():
    global __kicker_kill, __kicker_thread

    if __kicker_kill is not None:
        __kicker_kill.set()

    if __kicker_thread is not None:
        __kicker_thread.join()

    __kicker_kill = None
    __kicker_thread = None
