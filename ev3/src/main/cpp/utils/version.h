#include <string>
#include <vector>

#ifndef ROBOTS_EV3_UTILS_VERSION_H_
#define ROBOTS_EV3_UTILS_VERSION_H_

class Version {
    public:
        Version(std::string value);
        std::vector<char> toBytes();
        static Version fromBytes(std::vector<char> bytes);
    private:
        std::string value;
};

#endif