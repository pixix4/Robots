from enum import Enum


class EnergyState(Enum):
    CHARGING = 0
    DISCHARGING = 1
    UNKNOWN = 2
    NO_BATTERY = 3


class Energy:
    def __init__(self, value: float, state: EnergyState):
        self.value = value
        self.state = state

    def __str__(self):
        return str(self.value) + "," + str(self.state.name)

    @staticmethod
    def parse(value: str) -> "Energy":
        h = value.split(",")
        return Energy(float(h[0]), EnergyState[h[1]])
