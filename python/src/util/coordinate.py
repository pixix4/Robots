class Coordinate:
    def __init__(self, x: int, y: int, heading: int):
        self.x = x
        self.y = y
        self.heading = heading

    def __str__(self):
        return ",".join([str(it) for it in [self.x, self.y, self.heading]])

    @staticmethod
    def parse(value: str):
        h = value.split(",")
        return Coordinate(int(h[0]), int(h[1]), int(h[2]))

    def __eq__(self, other):
        if (not isinstance(other, Coordinate)) or (other is None):
            return False

        return self.x == other.x and self.y == other.y and self.heading == other.heading

    def __ne__(self, other):
        return not self.__eq__(other)
