#include <string>
#include <vector>

#ifndef ROBOTS_EV3_UTILS_ENERGY_H_
#define ROBOTS_EV3_UTILS_ENERGY_H_

enum EnergyState {
    CHARGING, DISCHARGING, UNKNOWN, NO_BATTERY
};

class Energy {
    public:
        Energy(float value, EnergyState state);
        std::vector<char> toBytes();
        static Energy fromBytes(std::vector<char> bytes);

    private:
        float value;
        EnergyState state;
};

#endif
