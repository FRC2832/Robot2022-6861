package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Turret;

public class AutoTurret extends CommandBase {
    private Turret turret;
    private double startPos;
    private double angle;
    private double sign;

    public AutoTurret(Turret turret, double angle) {
        this.turret = turret;
        addRequirements(turret);
        this.angle = Math.abs(angle);
        sign = Math.signum(angle);
    }

    @Override
    public void initialize() {
        //get the FL wheel distance when we start
        startPos = turret.getAngle();
    }

    @Override
    public void execute() {
        turret.setTurretSpeed(0.18 * sign);
    }

    @Override
    public boolean isFinished() {
        //FL wheel traveled 1 meter
        if((turret.getAngle() - startPos) * sign > angle) {
            return true;
        }
        return false;
    }

    @Override
    public void end(boolean interrupted) {
        turret.setTurretSpeed(0);
    }
}
