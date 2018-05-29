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
