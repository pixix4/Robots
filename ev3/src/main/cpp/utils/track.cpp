#include <string>
#include <vector>
#include <cmath>
#include <math.h>
#include "track.h"

using namespace std;

Track::Track(float x, float y) {
    this->x = x;
    this->y = y;
}

vector<char> Track::toBytes() {
    return vector<char>();
}

Track Track::fromBytes(vector<char> bytes) {
    return Track(0, 0);
}

float Track::radius() {
    return sqrt((this->x * this->x) + (this->y * this->y));
}

float Track::angle() {
    float radius = this->radius();

    if (radius == 0) {
        return 0;
    }

    if (this->y >= 0) {
        return acos(this->x / radius);
    }

    return 2 * M_PI - acos(this->x / radius);
}

bool Track::isZero() {
    return this->x == 0 && this->y == 0;
}

Track* Track::normalize() {
    float radius = this->radius();
    if (radius > 1.0) {
        return &Track(this->x / radius, this->y / radius);
    }
    return this;
}

Track* Track::copyWithRadius(float radius) {
    float oldRadius = this->radius();
    float r = fmax(radius, 0) / oldRadius;
    return &Track(this->x *r, this->y * r);
}
