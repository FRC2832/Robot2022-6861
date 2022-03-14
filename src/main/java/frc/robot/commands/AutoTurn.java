package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Drivetrain;

public class AutoTurn extends CommandBase {
    private Drivetrain drive;
    private double startAngle;
    private double angle;
    private double sign;

    public AutoTurn(Drivetrain drive, double angle) {
        this.drive = drive;
        addRequirements(drive);
        this.angle = Math.abs(angle);
        this.sign = Math.signum(angle);
    }

    @Override
    public void initialize() {
        startAngle = drive.getAngle();
    }

    @Override
    public void execute() {
        drive.drive(0, 0, 0.6 * sign, false);
    }

    @Override
    public boolean isFinished() {
        if((drive.getAngle() - startAngle) * sign > angle) {
            return true;
        }
        return false;
    }

    @Override
    public void end(boolean interrupted) {
        drive.drive(0, 0, 0, false);
    }
}
