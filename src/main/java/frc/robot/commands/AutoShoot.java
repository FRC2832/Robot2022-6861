package frc.robot.commands;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class AutoShoot extends CommandBase {
    private double startTime;

    public AutoShoot() {
        //this.drive = drive;
        //addRequirements(drive);
    }

    @Override
    public void initialize() {
        startTime = Timer.getFPGATimestamp();
    }

    @Override
    public void execute() {
    }

    @Override
    public boolean isFinished() {
        //if we have taken 2.5 seconds, then continue
        return (Timer.getFPGATimestamp() - startTime) > 2.5;
    }

    @Override
    public void end(boolean interrupted) {
    }
}
