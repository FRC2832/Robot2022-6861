// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;

/** This is a demo program showing how to use Mecanum control with the MecanumDrive class. */
public class Robot extends TimedRobot {
  private static final int kJoystickChannel = 0;
  private Joystick m_stick;
  private Drivetrain drive;
  private static Simulation sim;
  
  @Override
  public void robotInit() {
    drive = new Drivetrain();
    drive.register();

    m_stick = new Joystick(kJoystickChannel);
    sim = new Simulation(drive);
  }

  @Override
  public void teleopPeriodic() {
    //axis 0 = left stick left/right, 1 = left stick up/down, 4 = right stick left/right
     drive.drive(m_stick.getRawAxis(0), -m_stick.getRawAxis(1), m_stick.getRawAxis(4), false);
  }

  @Override
  public void simulationInit() {
      sim.init();
  }

  @Override
  public void simulationPeriodic() {
      sim.periodic();
  }

  public static Simulation getSim() {
      return sim;
  }
}
