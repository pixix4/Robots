use std::io::Result;
use std::sync::mpsc;
use std::sync::mpsc::{Receiver, Sender};
use std::thread;

use ev3dev_lang_rust::tacho_motor::{TachoMotor, LargeMotor, MediumMotor};
use ev3dev_lang_rust::core::MotorPort;
use network::NetworkCommand;

const MAX_SPEED: u8 = 100;

fn perform_drive(driving_receiver: &Receiver<DrivingCommand>) -> Result<()> {
    let mut pid_left_speed: f32 = 0.0;
    let mut pid_right_speed: f32 = 0.0;

    let mut left_speed: f32 = 0.0;
    let mut right_speed: f32 = 0.0;

    let speed: f32 = MAX_SPEED as f32;

    let mut kick = false;


    let mut right_motor = LargeMotor::new(MotorPort::OutA).unwrap();
    let mut left_motor = LargeMotor::new(MotorPort::OutB).unwrap();
    let mut kicker = MediumMotor::new(MotorPort::OutC).unwrap();

    left_motor.run_direct()?;
    right_motor.run_direct()?;

    loop {
        if let Ok(driving) = driving_receiver.recv() {
            match driving {
                DrivingCommand::SetTrack(left, right) => {
                    left_speed = left;
                    right_speed = right;
                }
                DrivingCommand::SetPid(left, right) => {
                    pid_left_speed = left;
                    pid_right_speed = right;
                }
                DrivingCommand::SetTrim(_) => {}
                DrivingCommand::Kick => {
                    kick = true;
                }
                DrivingCommand::Stop => {
                    return Ok(());
                }
            }

            let left = (pid_left_speed + left_speed).max(-1.0).min(1.0);
            let right = (pid_right_speed + right_speed).max(-1.0).min(1.0);

            left_motor.set_duty_cycle_sp((left * speed) as isize)?;
            right_motor.set_duty_cycle_sp((right * speed) as isize)?;
        }
    }
}

pub fn start() -> Sender<DrivingCommand> {
    let (driving_sender, driving_receiver) = mpsc::channel();

    thread::spawn(move || {
        loop {
            match perform_drive(&driving_receiver) {
                Ok(_) => {
                    break;
                }
                _ => {
                    println!("A drive error occurred, retry!");
                }
            }
        }
    });

    return driving_sender;
}

pub enum DrivingCommand {
    SetTrack(f32, f32),
    SetPid(f32, f32),
    SetTrim(f32),
    Kick,
    Stop,
}