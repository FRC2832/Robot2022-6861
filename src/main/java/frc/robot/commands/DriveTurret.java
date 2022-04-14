package frc.robot.commands;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Climber;
import frc.robot.Robot;
import frc.robot.Turret;

public class DriveTurret extends CommandBase{
    private Turret turret;
    private Climber climber;
    private XboxController operatorController;

    public DriveTurret(Turret turret, Climber climber, XboxController operatorController) {
        this.turret = turret;
        this.climber = climber;
        this.operatorController = operatorController;
        addRequirements(turret);
    }
    
    @Override
    public void execute() {
        double driverSpeed = -operatorController.getLeftX();
        
        if(climber.climbRequested()) {
            //we asked to climb, but turret is not at right position, so move turret instead
            turret.setTurretPosition(turret.SAFE_CLIMB_ANGLE + 3);
        } else if(Math.abs(driverSpeed) > 0.23) {  //driver deadband
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
