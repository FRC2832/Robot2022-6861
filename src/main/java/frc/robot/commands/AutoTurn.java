package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Drivetrain;

public class AutoTurn extends CommandBase {
    private Drivetrain drive;
    private double startAngle;
    private double angle;

    public AutoTurn(Drivetrain drive, double angle) {
        this.drive = drive;
        addRequirements(drive);
        this.angle = angle;
    }

    @Override
    public void initialize() {
        startAngle = drive.getAngle();
    }

    @Override
    public void execute() {
        drive.drive(0, 0, 0.6, false);
    }

    @Override
    public boolean isFinished() {
        if((drive.getAngle() - startAngle) > angle) {
            return true;
        }
        return false;
    }

    @Override
    public void end(boolean interrupted) {
        drive.drive(0, 0, 0, false);
    }
}