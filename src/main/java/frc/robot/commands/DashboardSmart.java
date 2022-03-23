package frc.robot.commands;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Intake;
import frc.robot.Shooter;
import frc.robot.ShooterConstants;

public class DashboardSmart extends CommandBase {
    private Shooter shooter;
    private XboxController operator;

    public DashboardSmart(Shooter shooter, XboxController operator) {
        this.shooter = shooter;
        this.operator = operator;
        addRequirements(shooter);
        //addRequirements(intake);
        //SmartDashboard.putNumber("Target RPM", ShooterConstants.DEFAULT_SHOT_RPM);
        //SmartDashboard.putNumber("Manual Hood", ShooterConstants.DEFAULT_SHOT_ANGLE);
    }
    
    @Override
    public void execute() {
        //shooter.setShooterRpm(ShooterConstants.DEFAULT_SHOT_RPM);
        shooter.setHoodAngle(ShooterConstants.DEFAULT_SHOT_ANGLE);

        //check shot speed is within 30 RPM
        shooter.setShooterRpm(ShooterConstants.DEFAULT_SHOT_RPM);
        if(Math.abs(ShooterConstants.DEFAULT_SHOT_RPM-shooter.getTargetRpm()) < 20)
        {
            operator.setRumble(RumbleType.kLeftRumble, 0.3);
        }
    }
}
