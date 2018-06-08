from math import sqrt, acos, pi


class Track:

    def __init__(self, x: float, y: float):
        self.x = x
        self.y = y

    def radius(self) -> float:
        return sqrt(self.x ** 2 + self.y ** 2)

    def angle(self) -> float:
        r = self.radius()
        if r == 0:
            return 0
        elif self.y >= 0:
            return acos(self.x / r)
        else:
            return 2 * pi - acos(self.x / r)

    def __str__(self):
        str(self.x) + "," + str(self.y)

    def normalize(self) -> "Track":
        r = self.radius()
        if r > 1.0:
            return Track(self.x / r, self.y / r)
        return self

    def is_zero(self) -> bool:
        return (self.x == 0) and (self.y == 0)

    def copy_with_radius(self, new_radius: float) -> "Track":
        old_radius = self.radius()
        r = max(new_radius, 0) / old_radius
        return Track(self.x * r, self.y * r)

    @staticmethod
    def parse(value: str) -> "Track":
        h = value.split(',')
        return Track(float(h[0]), float(h[1]))
