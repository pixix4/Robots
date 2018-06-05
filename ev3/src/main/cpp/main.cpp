#include <iostream>
#include <vector>
#include <random>
#include <chrono>
#include <ev3dev.h>

int main() {
    typedef std::chrono::high_resolution_clock Clock;
    typedef std::chrono::milliseconds ms;
    typedef std::chrono::duration<double> sec;

    ev3dev::medium_motor motor = ev3dev::medium_motor(ev3dev::OUTPUT_C);

    Clock::time_point start = Clock::now();

    std::default_random_engine generator;
    std::uniform_int_distribution<int> distribution(0, 1000000);

    const int repeats = 10;
    std::vector<long> times;
    times.reserve(repeats);

    for (int r = 0; r < repeats; r++) {
        std::cout << "Try " << r+1 << "\n";
        Clock::time_point t0 = Clock::now();

        std::vector<int> v;
        for (int i = 0; i < 100000; i++) {
            v.push_back(distribution(generator));
        }

        Clock::time_point t1 = Clock::now();
        sec fs = t1 - t0;
        ms d = std::chrono::duration_cast<ms>(fs);

        times.push_back(d.count());
    }

    long sum = 0;
    for (auto &n : times)
        sum += n;
    sum /= repeats;

    Clock::time_point end = Clock::now();
    sec fsa = end - start;
    ms da = std::chrono::duration_cast<ms>(fsa);

    std::cout << sum << "ms\n";
    std::cout << da.count() << "ms\n";

    return 0;
}
