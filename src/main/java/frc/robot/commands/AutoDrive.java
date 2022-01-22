package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Drivetrain;

public class AutoDrive extends CommandBase {
    private Drivetrain drive;
    private double startPos;
    private double distance;

    public AutoDrive(Drivetrain drive, double distance) {
        this.drive = drive;
        addRequirements(drive);
        this.distance = distance;
    }

    @Override
    public void initialize() {
        //get the FL wheel distance when we start
        startPos = drive.getDistance(0);
    }

    @Override
    public void execute() {
        drive.drive(0, 0.6, 0, false);
    }

    @Override
    public boolean isFinished() {
        //FL wheel traveled 1 meter
        if((drive.getDistance(0) - startPos) > distance) {
            return true;
        }
        return false;
    }

    @Override
    public void end(boolean interrupted) {
        drive.drive(0, 0, 0, false);
    }
}
