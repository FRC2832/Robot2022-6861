package frc.robot.commands;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.CommandBase;
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
        turret.setTurretSpeed(-operatorController.getLeftX() * 0.25);
    }
}
