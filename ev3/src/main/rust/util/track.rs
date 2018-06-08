use std;
use std::f32::consts;
use std::string::String;

#[derive(PartialEq)]
pub struct Track {
    pub x: f32,
    pub y: f32,
}

impl std::string::ToString for Track {
    fn to_string(&self) -> String {
        format!("Track({}, {})", self.x, self.y)
    }
}

#[allow(dead_code)]
impl Track {
    pub fn radius(&self) -> f32 {
        (self.x * self.x + self.y * self.y).sqrt()
    }

    pub fn angle(&self) -> f32 {
        let r = self.radius();
        if r == 0.0 {
            0.0
        } else if self.y >= 0.0 {
            (self.x / r).acos()
        } else {
            2.0 * consts::PI - (self.x / r).acos()
        }
    }


    fn normalize(&self) -> Track {
        let r = self.radius();
        if r > 1.0 {
            Track {
                x: self.x / r,
                y: self.y / r,
            }
        } else {
            Track {
                x: self.x,
                y: self.y,
            }
        }
    }

    fn is_zero(&self) -> bool {
        (self.x == 0.0) && (self.y == 0.0)
    }

    fn copy_with_radius(&self, new_radius: f32) -> Track {
        let old_radius = self.radius();
        let r = new_radius.max(0.0) / old_radius;
        Track { x: self.x * r, y: self.y * r }
    }
}
