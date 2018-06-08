extern crate rand;
//extern crate regex;
extern crate time;

use std::vec::Vec;
use time::PreciseTime;
use time::Duration;
use util::Version;

mod util;

fn main() {
    println!("Hello, world!");
    println!("{}", Version::current().to_string());

    let mut times = Vec::new();
    let start_outer = PreciseTime::now();

    for i in 0..10 {
        println!("Try {}", i + 1);

        let mut rand = Vec::new();
        let start = PreciseTime::now();
        for j in 0..1000 {
            let r: u32 = rand::random();
            rand.push(r);
        }
        let stop = PreciseTime::now();

        times.push(start.to(stop).num_milliseconds());
    }
    let stop_outer = PreciseTime::now();

    let sum:i64 = times.iter().sum();
    let av = sum / times.len() as i64;
    println!("Average time: {}", sum);
    println!("Total time: {}", start_outer.to(stop_outer).num_milliseconds());
}
