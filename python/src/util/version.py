class Version:

    def __init__(self, value: str):
        self.value = value

    def __str__(self):
        return self.value

    @staticmethod
    def parse(value: str) -> "Version":
        return Version(value)
