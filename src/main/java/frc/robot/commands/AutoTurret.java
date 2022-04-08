package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Turret;

public class AutoTurret extends CommandBase {
    private Turret turret;
    private double angle;

    public AutoTurret(Turret turret, double angle) {
        this.turret = turret;
        addRequirements(turret);
        this.angle = Math.abs(angle);
    }

    @Override
    public void initialize() {
    }

    @Override
    public void execute() {
        turret.setTurretPosition(angle);
    }

    @Override
    public boolean isFinished() {
        if(Math.abs(turret.getAngle() - angle) < 5) {
            return true;
        }
        return false;
    }

    @Override
    public void end(boolean interrupted) {
        turret.setTurretSpeed(0);
    }
}
