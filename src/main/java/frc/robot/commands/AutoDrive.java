package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Drivetrain;

public class AutoDrive extends CommandBase {
    private Drivetrain drive;
    private double startPos;

    public AutoDrive(Drivetrain drive) {
        this.drive = drive;
        addRequirements(drive);
    }

    @Override
    public void initialize() {
        startPos = drive.getDistance(0);
    }

    @Override
    public void execute() {
        drive.drive(0, 0.6, 0, false);
    }

    @Override
    public boolean isFinished() {
        //FL wheel traveled 1 meter
        if((drive.getDistance(0) - startPos) > 1.7) {
            return true;
        }
        return false;
    }

    @Override
    public void end(boolean interrupted) {
        drive.drive(0, 0, 0, false);
    }
}
