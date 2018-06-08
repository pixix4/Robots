//use regex::Regex;
use std;
use std::string::String;
use std::vec::Vec;

#[derive(Ord, PartialOrd, Eq, PartialEq)]
enum Qualifier {
    None = 4,
    Rc = 3,
    Beta = 2,
    Alpha = 1,
    Snapshot = 0,
}

impl std::string::ToString for Qualifier {
    fn to_string(&self) -> String {
        match *self {
            Qualifier::Snapshot => "SNAPSHOT",
            Qualifier::Alpha => "ALPHA",
            Qualifier::Beta => "BETA",
            Qualifier::Rc => "RC",
            _ => "UNKNOWN"
        }.to_string()
    }
}

#[derive(Ord, PartialOrd, Eq, PartialEq)]
pub struct Version {
    major: u32,
    minor: u32,
    patch: u32,
    qualifier: Qualifier,
    qualifier_number: u32,
}

impl std::string::ToString for Version {
    fn to_string(&self) -> String {
        if self.qualifier == Qualifier::None {
            format!("{}.{}.{}", self.major, self.minor, self.patch)
        } else if self.qualifier_number == 0 {
            format!("{}.{}.{}-{}", self.major, self.minor, self.patch, self.qualifier.to_string())
        } else {
            format!("{}.{}.{}-{}.{}", self.major, self.minor, self.patch, self.qualifier.to_string(), self.qualifier_number)
        }
    }
}

impl Version {
    pub fn current() -> Version {
        Version::parse(env!("CARGO_PKG_VERSION").to_string())
    }

    pub fn unknown() -> Version {
        Version {
            major: 0,
            minor: 0,
            patch: 0,
            qualifier: Qualifier::None,
            qualifier_number: 0,
        }
    }

    pub fn parse(value: String) -> Version {
        let v1: Vec<&str> = value.split('-').collect();
        let v2: Vec<&str> = v1[0].split('.').collect();

        let major: u32 = v2[0].parse().unwrap_or(0);
        let minor: u32 = v2[1].parse().unwrap_or(0);
        let patch: u32 = v2[2].parse().unwrap_or(0);
        let mut qualifier = Qualifier::None;
        let mut qualifier_number: u32 = 0;

        if v1.len() > 1 {
            let v3: Vec<&str> = v1[1].split('.').collect();
            qualifier = match v3[0].to_lowercase().as_str() {
                "snapshot" => Qualifier::Snapshot,
                "alpha" => Qualifier::Alpha,
                "beta" => Qualifier::Beta,
                "rc" => Qualifier::Rc,
                _ => Qualifier::None
            };

            if v3.len() > 1 {
                qualifier_number = v3[1].parse().unwrap_or(0);
            }
        }

        Version { major, minor, patch, qualifier, qualifier_number }
    }
}