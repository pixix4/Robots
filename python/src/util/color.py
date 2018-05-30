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

    def __eq__(self, other: "Color"):
        if (not isinstance(other, Color)) or (other is None):
            return False

        return self.red == other.red and self.green == other.green and self.blue == other.blue and self.alpha == other.alpha

    def __ne__(self, other):
        return not self.__eq__(other)

    @staticmethod
    def parse(value: str) -> "Color":
        if value[0] == '#':
            value = value.lstrip('#')
            lv = len(value)
            red, green, blue = tuple(int(value[i:i + lv // 3], 16) for i in range(0, lv, lv // 3))
            return Color(red, green, blue)
        else:
            print("parse color {}".format(value[value.find("(") + 1:value.rfind(")")]))
            r, g, b, a = value[value.find("(") + 1:value.rfind(")")].split(",")
            return Color(int(r), int(g), int(b), float(a))


CONST_TRANSPARENT = Color.parse("rgba(0,0,0,0)")
CONST_WHITE = Color.parse("#FFFFFF")
CONST_BLACK = Color.parse("#000000")
CONST_RED = Color.parse("#F44336")
CONST_PINK = Color.parse("#E91E63")
CONST_PURPLE = Color.parse("#9C27B0")
CONST_DEEP_PURPLE = Color.parse("#673AB7")
CONST_INDIGO = Color.parse("#3F51B5")
CONST_BLUE = Color.parse("#2196F3")
CONST_LIGHT_BLUE = Color.parse("#03A9F4")
CONST_CYAN = Color.parse("#00BCD4")
CONST_TEAL = Color.parse("#009688")
CONST_GREEN = Color.parse("#4CAF50")
CONST_LIGHT_GREEN = Color.parse("#8BC34A")
CONST_LIME = Color.parse("#CDDC39")
CONST_YELLOW = Color.parse("#FFEB3B")
CONST_AMBER = Color.parse("#FFC107")
CONST_ORANGE = Color.parse("#FF9800")
CONST_DEEP_ORANGE = Color.parse("#FF5722")
CONST_BROWN = Color.parse("#795548")
CONST_GREY = Color.parse("#9E9E9E")
CONST_BLUE_GREY = Color.parse("#607D8B")
