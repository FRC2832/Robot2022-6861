package frc.robot.commands;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Drivetrain;

public class AutoDriveDiagonal extends CommandBase {
    private Drivetrain drive;
    private double startTime;
    private double xSpeed,ySpeed;
    private double time;
    

    public AutoDriveDiagonal(Drivetrain drive, double xSpeed, double ySpeed, double time) {
        this.drive = drive;
        addRequirements(drive);
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
        this.time = time;
    }

    @Override
    public void initialize() {
        startTime = Timer.getFPGATimestamp();
    }

    @Override
    public void execute() {
        drive.drive(xSpeed, ySpeed, 0, false);
    }

    @Override
    public boolean isFinished() {
        if((Timer.getFPGATimestamp() - startTime) > time) {
            return true;
        }
        return false;
    }

    @Override
    public void end(boolean interrupted) {
        drive.drive(0, 0, 0, false);
    }
}
