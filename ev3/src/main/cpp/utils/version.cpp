#include <string>
#include <vector>
#include "version.h"

using namespace std;

Version::Version(string value) {
    this->value = value;
}

vector<char> Version::toBytes() {
    return vector<char>();
}

Version Version::fromBytes(vector<char> bytes) {
    return Version("0.0.0");
}