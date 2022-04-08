package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Drivetrain;
import frc.robot.Pi;

public class AimDrive extends CommandBase {
    private Drivetrain drive;
    private double startPos;
    private double distance;
    private double sign;
    private Pi pi;

    public AimDrive(Drivetrain drive, Pi pi, double distance) {
        this.drive = drive;
        this.pi = pi;
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
        double center = pi.getCargoX();
        double angle;
        if (center > 0) {
            angle = (center - 160)/45; //160 is center, 45* angle FOV each side
        } else {
            angle = 0;
        }
        drive.drive(0, 0.2 * sign, angle / 10, false);
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
