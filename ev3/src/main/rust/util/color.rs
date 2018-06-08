use std;
use std::string::String;
use std::vec::Vec;

#[derive(PartialEq)]
pub struct Color {
    red: u16,
    green: u16,
    blue: u16,
    alpha: f32,
}


impl std::string::ToString for Color {
    fn to_string(&self) -> String {
        if self.alpha >= 1.0 {
            format!("#{0:02x}{1:02x}{2:02x}", self.red, self.green, self.blue)
        } else {
            format!("rgba({}, {}, {}, {})", self.red, self.green, self.blue, self.alpha)
        }
    }
}

impl Color {
    fn parse(value: String) -> Color {
        if value.starts_with("#") {
            let red = u16::from_str_radix(&value[1..3], 16).unwrap_or(0);
            let green = u16::from_str_radix(&value[3..5], 16).unwrap_or(0);
            let blue = u16::from_str_radix(&value[5..7], 16).unwrap_or(0);

            Color { red, green, blue, alpha: 1.0 }
        } else {
            let v: Vec<&str> = value[value.find('(').unwrap_or(0)..value.find(')').unwrap_or(value.len())].split(',').collect();
            let red: u16 = v[0].parse().unwrap_or(0);
            let green: u16 = v[1].parse().unwrap_or(0);
            let blue: u16 = v[2].parse().unwrap_or(0);
            let alpha: f32 = v[3].parse().unwrap_or(1.0);

            Color { red, green, blue, alpha: alpha }
        }
    }

    fn transparent() -> Color { Color::parse("rgba(0,0,0,0)".to_string()) }
    fn white() -> Color { Color::parse("#FFFFFF".to_string()) }
    fn black() -> Color { Color::parse("#000000".to_string()) }
    fn red() -> Color { Color::parse("#F44336".to_string()) }
    fn pink() -> Color { Color::parse("#E91E63".to_string()) }
    fn purple() -> Color { Color::parse("#9C27B0".to_string()) }
    fn deep_purple() -> Color { Color::parse("#673AB7".to_string()) }
    fn indigo() -> Color { Color::parse("#3F51B5".to_string()) }
    fn blue() -> Color { Color::parse("#2196F3".to_string()) }
    fn light_blue() -> Color { Color::parse("#03A9F4".to_string()) }
    fn cyan() -> Color { Color::parse("#00BCD4".to_string()) }
    fn teal() -> Color { Color::parse("#009688".to_string()) }
    fn green() -> Color { Color::parse("#4CAF50".to_string()) }
    fn light_green() -> Color { Color::parse("#8BC34A".to_string()) }
    fn lime() -> Color { Color::parse("#CDDC39".to_string()) }
    fn yellow() -> Color { Color::parse("#FFEB3B".to_string()) }
    fn amber() -> Color { Color::parse("#FFC107".to_string()) }
    fn orange() -> Color { Color::parse("#FF9800".to_string()) }
    fn deep_orange() -> Color { Color::parse("#FF5722".to_string()) }
    fn brown() -> Color { Color::parse("#795548".to_string()) }
    fn grey() -> Color { Color::parse("#9E9E9E".to_string()) }
    fn blue_grey() -> Color { Color::parse("#607D8B".to_string()) }
}
