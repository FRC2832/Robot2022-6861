package frc.robot.commands;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Robot;
import frc.robot.Shooter;

public class DriveHood extends CommandBase{
    private Shooter shooter;
    private XboxController operatorController;

    public DriveHood(Shooter shooter, XboxController operatorController) {
        this.shooter = shooter;
        this.operatorController = operatorController;
        addRequirements(shooter);
    }
    
    @Override
    public void execute() {
        if(Robot.isAutoShootEnabled()) {
            shooter.setHoodAngle(shooter.getTargetHoodAngle());
            shooter.setShooterRpm(shooter.getTargetRpm());
        } else {
            shooter.setHoodSpeedPct(-operatorController.getRightY()*0.25);
            shooter.setShootPct(0);
        }
    }
}
