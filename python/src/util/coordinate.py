class Coordinate:
    def __init__(self, x: int, y: int):
        self.x = x
        self.y = y

    def __str__(self):
        return str(self.x) + "," + str(self.y)

    @staticmethod
    def parse(value: str):
        h = value.split(",")
        return Coordinate(int(h[0]), int(h[1]))
