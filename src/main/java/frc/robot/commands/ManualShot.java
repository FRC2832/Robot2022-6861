package frc.robot.commands;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Shooter;
import frc.robot.ShooterConstants;

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
    public void execute() {
        shooter.setShooterRpm(rpm);
        shooter.setHoodAngle(hood);
    }
}
