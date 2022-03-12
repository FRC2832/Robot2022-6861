package frc.robot.commands;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Shooter;
import frc.robot.ShooterConstants;

public class DashboardShoot extends CommandBase {
    private Shooter shooter;

    public DashboardShoot(Shooter shooter) {
        this.shooter = shooter;
        addRequirements(shooter);
        SmartDashboard.putNumber("Target RPM", ShooterConstants.DEFAULT_SHOT_RPM);
    }
    
    @Override
    public void execute() {
        double rpm = SmartDashboard.getNumber("Target RPM", ShooterConstants.DEFAULT_SHOT_RPM);
        shooter.setShooterRpm(rpm);
        shooter.setHoodAngle(ShooterConstants.DEFAULT_SHOT_ANGLE);
    }
}
