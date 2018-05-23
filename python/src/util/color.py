class Color:
    def __init__(self, red: int = 0, green: int = 0, blue: int = 0, alpha: float = 1):
        self.red = red
        self.green = green
        self.blue = blue
        self.alpha = alpha

    def __str__(self):
        if self.alpha >= 1:
            return "#{0:02x}{1:02x}{2:02x}".format(self.red, self.green, self.blue)
        else:
            return "rgba({},{},{},{})".format(self.red, self.green, self.blue, self.alpha)

    @staticmethod
    def parse(value: str) -> "Color":
        if value[0] == '#':
            value = value.lstrip('#')
            lv = len(value)
            red, green, blue = tuple(int(value[i:i + lv // 3], 16) for i in range(0, lv, lv // 3))
            return Color(red, green, blue)
        else:
            r, g, b, a = value[value.find("("):value.rfind(")")].split(",")
            return Color(int(r), int(g), int(b), float(a))
