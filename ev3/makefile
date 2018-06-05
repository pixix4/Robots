CXX = g++
BINARY = ev3-native

SOURCES := $(shell find src/main/cpp -name '*.cpp')
LIBS := $(shell find libs -name '*.cpp')

all: ${BINARY}

${BINARY}: src/main/cpp/main.cpp mkbuild
	${CXX} -Ilibs -std=c++11 -o build/${BINARY} ${SOURCES} ${LIBS}

mkbuild:
	mkdir -p build

clean:
	rm build/${BINARY}
