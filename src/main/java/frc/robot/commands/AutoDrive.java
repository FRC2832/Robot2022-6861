package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Drivetrain;

public class AutoDrive extends CommandBase {
    private Drivetrain drive;
    private double startPos;
    private double distance;
    private double sign;

    public AutoDrive(Drivetrain drive, double distance) {
        this.drive = drive;
        addRequirements(drive);
        this.distance = Math.abs(distance);
        sign = Math.signum(distance);
    }

    @Override
    public void initialize() {
        //get the FL wheel distance when we start
        startPos = drive.getDistance(0);
    }

    @Override
    public void execute() {
        drive.drive(0, 0.6 * sign, 0, false);
    }

    @Override
    public boolean isFinished() {
        //FL wheel traveled 1 meter
        if((drive.getDistance(0) - startPos) * sign > distance) {
            return true;
        }
        return false;
    }

    @Override
    public void end(boolean interrupted) {
        drive.drive(0, 0, 0, false);
    }
}
