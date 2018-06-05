#include <string>
#include <vector>

#ifndef ROBOTS_EV3_UTILS_TRACK_H_
#define ROBOTS_EV3_UTILS_TRACK_H_

class Track {
    public:
        Track(float x, float y);
        std::vector<char> toBytes();
        static Track fromBytes(std::vector<char> bytes);

        float radius();
        float angle();
        bool isZero();
        Track* normalize();
        Track* copyWithRadius(float radius);
    private:
        float x;
        float y;
};

#endif
