enum EnergyState {
    Charging = 0,
    Discharging = 1,
    Unknown = 2,
    NoBattery = 3,
}

pub struct Energy {
    value: f32,
    state: EnergyState
}

