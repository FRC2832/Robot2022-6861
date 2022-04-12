package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Robot;
import frc.robot.Shooter;

public class ManualShot extends CommandBase {
    private Shooter shooter;
    private double rpm;
    private double hood;

    public ManualShot(Shooter shooter, double rpm, double hood) {
        this.shooter = shooter;
        this.rpm = rpm;
        this.hood = hood;
        addRequirements(shooter);
    }

    @Override
    public void initialize() {
        Robot.snapHub("PAD");
    }
    
    @Override
    public void execute() {
        shooter.setShooterRpm(rpm);
        shooter.setHoodAngle(hood);
    }
}
