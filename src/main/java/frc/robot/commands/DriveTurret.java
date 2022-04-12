package frc.robot.commands;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Robot;
import frc.robot.Turret;

public class DriveTurret extends CommandBase{
    private Turret turret;
    private XboxController operatorController;

    public DriveTurret(Turret turret, XboxController operatorController) {
        this.turret = turret;
        this.operatorController = operatorController;
        addRequirements(turret);
    }
    
    @Override
    public void execute() {
        double driverSpeed = -operatorController.getLeftX();
        
        if(Math.abs(driverSpeed) > 0.13) {  //driver deadband
            turret.setTurretSpeed(driverSpeed * 0.25);
        } else if(Robot.isAutoShootEnabled()) {
            double turretAim = turret.getTurretAimAngle();
            if(turretAim > 0) {
                turret.setTurretPosition(turretAim);
            } else {
                turret.setTurretSpeed(0);
            }
        } else {
            turret.setTurretSpeed(0);
        }
    }
}
