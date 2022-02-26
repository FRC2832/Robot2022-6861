package frc.robot.commands;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Drivetrain;

public class DriveStick extends CommandBase {
    private Joystick m_stick;
    private Drivetrain drive;

    public DriveStick(Drivetrain drive, Joystick stick) {
        this.drive = drive;
        m_stick = stick;
        addRequirements(drive);
    }

    @Override
    public void initialize() {
        
    }

    @Override
    public void execute() {
        //axis 0 = left stick left/right, 1 = left stick up/down, 2 = right stick left/right

        double xSpeed = curveDriveStick(deadband(m_stick.getRawAxis(0)));
        double ySpeed = curveDriveStick(deadband(m_stick.getRawAxis(1)));
        double rot = curveDriveStick(deadband(m_stick.getRawAxis(2)));
        
        drive.drive(xSpeed, -ySpeed, rot*0.5, false);
    }

    private static double curveDriveStick (double input) {
        final double a = 0.5;       //if a=0, driver input is linear, if a=1, driver input cubed, 0.5 = squared (roughly)
        final double b = 0;       //deadband constant

        if(input >=0) {
            return b + (1-b)*(a*Math.pow(input, 3) + (1-a)*input);
        } else {
            return -b + (1-b)*(a*Math.pow(input, 3) + (1-a)*input);
        }
    }

    private static double deadband(double input) {
        final double DEADBAND = 0.13; 
        double abs = Math.abs(input);

        if (abs > DEADBAND) {
            return Math.signum(input) * ((abs-DEADBAND)/(1-DEADBAND));
        } else {
            return 0;
        }
    }
}
