import threading
from typing import Tuple

import devices
import drive

__foreground: Tuple[int, int, int] = (20, 20, 20)
__background: Tuple[int, int, int] = (180, 170, 130)


def foreground():
    global __foreground
    __foreground = devices.current_color()


def background():
    global __background
    __background = devices.current_color()


__pid_kill: threading.Event = None
__pid_thread: threading.Thread = None

CONST_PROPORTIONAL = 0.4
CONST_INTEGRAL = 0.18
CONST_DERIVATIVE = 0.25
INTEGRAL_MAXIMUM = 2.5
INTEGRAL_LIMITER = (INTEGRAL_MAXIMUM - 1.0) / INTEGRAL_MAXIMUM
SPEED = 0.6
SPEED_FAST = SPEED + 0.4
SPEED_NORMAL = SPEED
SPEED_SLOW = SPEED - 0.2
COUNTERMEASURE = 0.5
DRIVE_MULTIPLIER: float = -1


def start():
    print("Start pid")
    global __pid_kill
    if running():
        return

    __pid_kill = threading.Event()
    __pid_thread = threading.Thread(target=thread, name="pid", args=(__pid_kill,))
    __pid_thread.daemon = True
    __pid_thread.start()


def calc_error() -> float:
    sensor: Tuple[int, int, int] = devices.current_color()
    red = (sensor[0] - __foreground[0]) / float(__background[0] - __foreground[0])
    green = (sensor[1] - __foreground[1]) / float(__background[1] - __foreground[1])
    blue = (sensor[2] - __foreground[2]) / float(__background[2] - __foreground[2])
    return (max(min((red + green + blue) / 1.5, 2.0), 0.0) - 1.0) * DRIVE_MULTIPLIER


def thread(kill_event, arg):
    history_error: float = 0.0
    last_error: float = 0.0
    integral: float = 0.0
    dt: float = 1.0
    lost_line = 0
    drive_slow = 0

    while not kill_event.wait(0):
        error = calc_error()

        integral = (integral + error * dt) * INTEGRAL_LIMITER
        derivative = (error - last_error) / dt

        output = CONST_PROPORTIONAL * error + CONST_INTEGRAL * integral + CONST_DERIVATIVE * derivative

        if lost_line > 15:
            print("search line")
            integral = INTEGRAL_MAXIMUM * DRIVE_MULTIPLIER
            history_error = 0
            last_error = 0
            lost_line = 0

            while calc_error() * DRIVE_MULTIPLIER > -0.5:
                drive.direct(SPEED_SLOW * DRIVE_MULTIPLIER, -SPEED_SLOW * DRIVE_MULTIPLIER)

            drive_slow = 20
            continue

        if lost_line > 0:
            if error * DRIVE_MULTIPLIER > 0.5:
                lost_line += 1
            else:
                lost_line = 0

        if abs(history_error - error) > 0.7 and error * DRIVE_MULTIPLIER > 0.5 and drive_slow == 0:
            lost_line += 1

        history_error = last_error
        last_error = error

        speed = SPEED_NORMAL
        if drive_slow > 0:
            drive_slow -= 1
            speed = SPEED_SLOW
        elif abs(integral) < 0.2 and abs(derivative) < 0.2:
            speed = SPEED_FAST
        elif abs(integral) > 1:
            speed = SPEED_SLOW

        # print("e: {} | o: {} | p: {} | i: {} | d: {}".format(error, output, error * CONST_PROPORTIONAL,
        #                                                      integral * CONST_INTEGRAL,
        #                                                      derivative * CONST_DERIVATIVE))

        drive.direct((speed + (COUNTERMEASURE * output)), (speed - (COUNTERMEASURE * output)))

    drive.stop()


def stop():
    global __pid_kill, __pid_thread
    if not running():
        return

    __pid_kill.set()
    __pid_thread.join()

    __pid_kill = None
    __pid_thread = None

    drive.direct(0.0, 0.0)


def running() -> bool:
    return __pid_kill is not None
