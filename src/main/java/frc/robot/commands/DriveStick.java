package frc.robot.commands;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Drivetrain;

public class DriveStick extends CommandBase {
    private Joystick leftStick;
    private Joystick rightStick;
    private Drivetrain drive;

    public DriveStick(Drivetrain drive, Joystick leftStick, Joystick rightStick) {
        this.drive = drive;
        this.leftStick = leftStick;
        this.rightStick = rightStick;
        addRequirements(drive);
    }

    @Override
    public void initialize() {
        
    }

    @Override
    public void execute() {
        //axis 0 = left stick left/right, 1 = left stick up/down, 2 = right stick left/right

        /* old 1 stick
        double xSpeed = curveDriveStick(deadband(leftStick.getRawAxis(0)));
        double ySpeed = curveDriveStick(deadband(leftStick.getRawAxis(1)));
        double rot = curveDriveStick(deadband(leftStick.getRawAxis(2)));
        drive.drive(xSpeed, -ySpeed, rot*0.5, false);
        */

        double xSpeed0 = curveDriveStick(deadband(leftStick.getRawAxis(0)));
        double ySpeed0 = curveDriveStick(deadband(leftStick.getRawAxis(1)));
        double xSpeed1 = curveDriveStick(deadband(rightStick.getRawAxis(0)));
        double ySpeed1 = curveDriveStick(deadband(rightStick.getRawAxis(1)));
        drive.driveMechanumTank(xSpeed0, ySpeed0, xSpeed1, ySpeed1);
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
